package com.mattermost.integration.figma.api.figma.notification.service;

import com.mattermost.integration.figma.api.figma.webhook.dto.TeamWebhookInfoResponseDto;
import com.mattermost.integration.figma.api.figma.webhook.dto.Webhook;
import com.mattermost.integration.figma.api.figma.webhook.service.FigmaWebhookService;
import com.mattermost.integration.figma.api.mm.dm.service.DMMessageSenderService;
import com.mattermost.integration.figma.api.mm.kv.KVService;
import com.mattermost.integration.figma.api.mm.kv.SubscriptionKVService;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.input.figma.notification.FigmaWebhookResponse;
import com.mattermost.integration.figma.input.figma.notification.FileCommentNotificationRequest;
import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.input.mm.form.MMStaticSelectField;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.service.OAuthService;
import com.mattermost.integration.figma.subscribe.service.SubscribeService;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@Slf4j
public class FileNotificationServiceImpl implements FileNotificationService {
    private static final String BASE_WEBHOOK_URL = "https://api.figma.com/v2/webhooks";
    private static final String PASSCODE = "Mattermost";
    private static final String FILE_COMMENT_EVENT_TYPE = "FILE_COMMENT";
    private static final String REDIRECT_URL = "%s%s?secret=%s";
    private static final String FILE_COMMENT_URL = "/webhook/comment";
    private static final String MENTIONED_NOTIFICATION_ROOT = "commented on";

    @Autowired
    @Qualifier("figmaRestTemplate")
    private RestTemplate figmaRestTemplate;
    @Autowired
    private FigmaWebhookService figmaWebhookService;
    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private UserDataKVService userDataKVService;
    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private DMMessageSenderService dmMessageSenderService;
    @Autowired
    private KVService kvService;
    @Autowired
    private JsonUtils jsonUtils;


    public void sendFileNotificationMessageToMMSubscribedChannels(FileCommentWebhookResponse fileCommentWebhookResponse) {

        FigmaWebhookResponse figmaData = fileCommentWebhookResponse.getValues().getData();
        Set<String> mmSubscribedChannels = subscribeService.getMMChannelIdsByFileId(fileCommentWebhookResponse.getContext(), figmaData.getFileKey());
        mmSubscribedChannels.forEach(ch -> dmMessageSenderService.sendMessageToSubscribedChannel(ch, fileCommentWebhookResponse));
    }

    public SubscribeToFileNotification subscribeToFileNotification(InputPayload inputPayload) {
        String teamId = inputPayload.getValues().getTeamId();
        if (teamId == null || teamId.isEmpty() || teamId.isBlank()) {
            return SubscribeToFileNotification.BAD_TEAM_ID;
        }

        String mmSiteUrl = inputPayload.getContext().getMattermostSiteUrl();
        String botAccessToken = inputPayload.getContext().getBotAccessToken();

        String accessToken = getToken(inputPayload);

        deleteExistingFileCommentWebhook(teamId, accessToken, mmSiteUrl, botAccessToken);

        HttpEntity<FileCommentNotificationRequest> request = createFileCommentNotificationRequest(inputPayload, accessToken);
        log.debug("File notification request : " + request);
        log.info("Sending comment request for team with id: " + teamId);
        ResponseEntity<String> stringResponseEntity = figmaRestTemplate.postForEntity(BASE_WEBHOOK_URL, request, String.class);
        Webhook webhook = (Webhook) jsonUtils.convertStringToObject(stringResponseEntity.getBody(), Webhook.class).get();
        kvService.put(webhook.getId(), inputPayload.getContext().getOauth2().getUser().getUserId(), mmSiteUrl, botAccessToken);
        return SubscribeToFileNotification.SUBSCRIBED;
    }

    public void sendFileNotificationMessageToMM(FileCommentWebhookResponse fileCommentWebhookResponse) {
        FigmaWebhookResponse figmaWebhookResponse = fileCommentWebhookResponse.getValues().getData();
        Context context = fileCommentWebhookResponse.getContext();
        String mattermostSiteUrl = context.getMattermostSiteUrl();
        String botAccessToken = context.getBotAccessToken();
        String commenterTeamId = figmaWebhookService.getCurrentUserTeamId(figmaWebhookResponse.getWebhookId(),
                mattermostSiteUrl, botAccessToken);

        userDataKVService.saveUserToCertainTeam(commenterTeamId, figmaWebhookResponse.getTriggeredBy().getId(),
                mattermostSiteUrl, botAccessToken);

        String fileOwnerId = dmMessageSenderService.sendMessageToFileOwner(figmaWebhookResponse, context);
        figmaWebhookResponse.getMentions().removeIf(mention -> mention.getId().equals(fileOwnerId));

        if (!isBlank(figmaWebhookResponse.getParentId())) {
            String authorId = dmMessageSenderService.sendMessageToCommentAuthor(figmaWebhookResponse, context, fileOwnerId);
            figmaWebhookResponse.getMentions().removeIf(mention -> mention.getId().equals(authorId));
        }
        if (!figmaWebhookResponse.getMentions().isEmpty()) {
            figmaWebhookResponse.getMentions().stream().distinct().map((mention -> userDataKVService.getUserData(mention.getId(), mattermostSiteUrl, botAccessToken)))
                    .forEach(userData -> dmMessageSenderService.sendMessageToSpecificReceiver(context, userData, figmaWebhookResponse, MENTIONED_NOTIFICATION_ROOT));
        }
    }

    private String getToken(InputPayload inputPayload) {
        String refreshToken = inputPayload.getContext().getOauth2().getUser().getRefreshToken();
        String clientId = inputPayload.getContext().getOauth2().getClientId();
        String clientSecret = inputPayload.getContext().getOauth2().getClientSecret();

        FigmaOAuthRefreshTokenResponseDTO refreshTokenDTO = oAuthService.refreshToken(clientId, clientSecret, refreshToken);
        return refreshTokenDTO.getAccessToken();
    }

    private void deleteExistingFileCommentWebhook(String teamId, String figmaToken, String mmSiteUrl, String botAccessToken) {
        TeamWebhookInfoResponseDto teamWebhooks = figmaWebhookService.getTeamWebhooks(teamId, figmaToken);
        teamWebhooks.webhooks.stream().filter(webhook -> FILE_COMMENT_EVENT_TYPE.equals(webhook.getEventType()))
                .forEach(webhook -> {
                    figmaWebhookService.deleteWebhook(webhook.getId(), figmaToken);
                    kvService.delete(webhook.getId(), mmSiteUrl, botAccessToken);
                });
    }

    private HttpEntity<FileCommentNotificationRequest> createFileCommentNotificationRequest(InputPayload inputPayload, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("Bearer %s", token));

        FileCommentNotificationRequest fileCommentNotificationRequest = new FileCommentNotificationRequest();
        fileCommentNotificationRequest.setEventType(FILE_COMMENT_EVENT_TYPE);
        fileCommentNotificationRequest.setTeamId(inputPayload.getValues().getTeamId());
        fileCommentNotificationRequest.setPasscode(PASSCODE);
        fileCommentNotificationRequest.setEndpoint(String.format(
                inputPayload.getContext().getMattermostSiteUrl().concat(REDIRECT_URL),
                inputPayload.getContext().getAppPath(), FILE_COMMENT_URL,
                inputPayload.getContext().getApp().getWebhookSecret()));

        return new HttpEntity<>(fileCommentNotificationRequest, headers);
    }
}
