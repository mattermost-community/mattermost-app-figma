package com.mattermost.integration.figma.api.figma.notification.service;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.api.figma.file.service.FigmaFileService;
import com.mattermost.integration.figma.api.figma.project.dto.ProjectDTO;
import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.api.figma.project.service.FigmaProjectService;
import com.mattermost.integration.figma.api.figma.webhook.dto.Webhook;
import com.mattermost.integration.figma.api.figma.webhook.service.FigmaWebhookService;
import com.mattermost.integration.figma.api.mm.dm.service.DMMessageSenderService;
import com.mattermost.integration.figma.api.mm.kv.KVService;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaCannotCreateWebhookException;
import com.mattermost.integration.figma.input.figma.notification.FigmaWebhookResponse;
import com.mattermost.integration.figma.input.figma.notification.FileCommentNotificationRequest;
import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.dto.UserDataDto;
import com.mattermost.integration.figma.security.service.OAuthService;
import com.mattermost.integration.figma.subscribe.service.SubscribeService;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.mattermost.integration.figma.constant.prefixes.webhook.TeamWebhookPrefixes.TEAM_WEBHOOK_PREFIX;
import static com.mattermost.integration.figma.constant.prefixes.webhook.TeamWebhookPrefixes.WEBHOOK_ID_PREFIX;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@Slf4j
public class FileNotificationServiceImpl implements FileNotificationService {
    private static final String BASE_WEBHOOK_URL = "https://api.figma.com/v2/webhooks";
    private static final String PASSCODE = "Mattermost";
    private static final String FILE_COMMENT_EVENT_TYPE = "FILE_COMMENT";
    private static final String REDIRECT_URL = "%s%s";
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
    @Autowired
    private FigmaProjectService figmaProjectService;
    @Autowired
    private FigmaFileService figmaFileService;


    public void sendFileNotificationMessageToMMSubscribedChannels(FileCommentWebhookResponse fileCommentWebhookResponse) {
        FigmaWebhookResponse figmaData = fileCommentWebhookResponse.getValues().getData();
        Set<String> mmSubscribedChannels = subscribeService.getMMChannelIdsByFileId(fileCommentWebhookResponse.getContext(), figmaData.getFileKey());
        mmSubscribedChannels.forEach(ch -> dmMessageSenderService.sendMessageToSubscribedChannel(ch, fileCommentWebhookResponse));
        sendNotificationsForSubscribedProjects(fileCommentWebhookResponse);
    }

    public void createTeamWebhook(InputPayload inputPayload) {

        String mmSiteUrl = inputPayload.getContext().getMattermostSiteUrl();
        String botAccessToken = inputPayload.getContext().getBotAccessToken();
        String teamId = inputPayload.getValues().getTeamId();

        String currentTeamWebhookId = kvService.get(TEAM_WEBHOOK_PREFIX.concat(teamId), mmSiteUrl, botAccessToken);
        String accessToken = getToken(inputPayload);

        Optional<Webhook> webhookOptional = tryToCreateWebhook(inputPayload, accessToken, teamId);
        if (webhookOptional.isPresent()) {
            deleteSingleFileCommentWebhook(currentTeamWebhookId, teamId, mmSiteUrl, botAccessToken);
            saveNecessaryDataToKv(inputPayload, webhookOptional.get());
            return;
        }

        if (StringUtils.isNotBlank(currentTeamWebhookId)) {
            return;
        }

        throw new FigmaCannotCreateWebhookException();
    }

    private Optional tryToCreateWebhook(InputPayload payload, String accessToken, String teamId) {
        HttpEntity<FileCommentNotificationRequest> request = createFileCommentNotificationRequest(payload, accessToken);
        log.debug("File notification request : " + request);
        log.info("Sending comment request for team with id: " + teamId);
        ResponseEntity<String> stringResponseEntity;
        try {
            stringResponseEntity = figmaRestTemplate.postForEntity(BASE_WEBHOOK_URL, request, String.class);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }

        return  Optional.of(jsonUtils.convertStringToObject(stringResponseEntity.getBody(), Webhook.class).orElse(Optional.empty()));
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

            if (StringUtils.isBlank(authorId)) {
                return;
            }

            figmaWebhookResponse.getMentions().removeIf(mention -> mention.getId().equals(authorId));
        }
        if (!figmaWebhookResponse.getMentions().isEmpty()) {
            figmaWebhookResponse.getMentions().stream().distinct().map((mention -> userDataKVService.getUserData(mention.getId(), mattermostSiteUrl, botAccessToken)))
                    .filter(Optional::isPresent).map(Optional::get)
                    .filter(UserDataDto::isConnected)
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

    private void deleteSingleFileCommentWebhook(String webhookId, String teamId, String mmSiteUrl, String botAccessToken) {
        String teamWebhookId = kvService.get(TEAM_WEBHOOK_PREFIX.concat(teamId), mmSiteUrl, botAccessToken);
        if (Objects.nonNull(teamWebhookId) && !teamWebhookId.isBlank()) {
            String webhookOwnerId = kvService.get(WEBHOOK_ID_PREFIX.concat(teamWebhookId), mmSiteUrl, botAccessToken);
            Optional<UserDataDto> webhookOwnerOptional = userDataKVService.getUserData(webhookOwnerId, mmSiteUrl, botAccessToken);
            if (webhookOwnerOptional.isEmpty()) {
                return;
            }
            UserDataDto webhookOwner = webhookOwnerOptional.get();
            FigmaOAuthRefreshTokenResponseDTO figmaOAuthRefreshTokenResponseDTO = oAuthService.refreshToken(webhookOwner.getClientId(), webhookOwner.getClientSecret(), webhookOwner.getRefreshToken());
            figmaWebhookService.deleteWebhook(webhookId, figmaOAuthRefreshTokenResponseDTO.getAccessToken());
            kvService.delete(WEBHOOK_ID_PREFIX.concat(webhookId), mmSiteUrl, botAccessToken);
            kvService.delete(TEAM_WEBHOOK_PREFIX.concat(teamId), mmSiteUrl, botAccessToken);
        }
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
                inputPayload.getContext().getAppPath(), FILE_COMMENT_URL));

        return new HttpEntity<>(fileCommentNotificationRequest, headers);
    }

    private void saveNecessaryDataToKv(InputPayload inputPayload, Webhook webhook) {
        String teamId = inputPayload.getValues().getTeamId();
        String mmSiteUrl = inputPayload.getContext().getMattermostSiteUrl();
        String botAccessToken = inputPayload.getContext().getBotAccessToken();

        kvService.put(WEBHOOK_ID_PREFIX.concat(webhook.getId()), inputPayload.getContext().getOauth2().getUser().getUserId(), mmSiteUrl, botAccessToken);
        kvService.put(TEAM_WEBHOOK_PREFIX.concat(teamId), webhook.getId(), mmSiteUrl, botAccessToken);
        userDataKVService.saveNewTeamToAllTeamIdsSet(teamId, mmSiteUrl, botAccessToken);
    }

    private void sendNotificationsForSubscribedProjects(FileCommentWebhookResponse fileCommentWebhookResponse) {
        FigmaWebhookResponse figmaData = fileCommentWebhookResponse.getValues().getData();
        Context context = fileCommentWebhookResponse.getContext();
        String mattermostSiteUrl = context.getMattermostSiteUrl();
        String botAccessToken = context.getBotAccessToken();
        String commenterTeamId = figmaWebhookService.getCurrentUserTeamId(figmaData.getWebhookId(),
                mattermostSiteUrl, botAccessToken);
        String webhookOwnerId = kvService.get(WEBHOOK_ID_PREFIX.concat(figmaData.getWebhookId()), mattermostSiteUrl, botAccessToken);

        Optional<TeamProjectDTO> teamProjects = figmaProjectService.getProjectsByTeamId(commenterTeamId, webhookOwnerId, mattermostSiteUrl, botAccessToken);

        if (teamProjects.isEmpty()) {
            return;
        }

        for (ProjectDTO projectDTO : teamProjects.get().getProjects()) {
            Optional<FigmaProjectFilesDTO> filesDTO = figmaFileService.getProjectFiles(projectDTO.getId(), webhookOwnerId, mattermostSiteUrl, botAccessToken);
            if (filesDTO.isEmpty()) {
                continue;
            }
            List<FigmaProjectFileDTO> projectFiles = filesDTO.get().getFiles();
            Optional<FigmaProjectFileDTO> triggeredFile = projectFiles.stream().filter(file -> file.getKey().equals(figmaData.getFileKey())).findFirst();
            if (triggeredFile.isPresent()) {
                Set<String> channelIds = subscribeService.getMMChannelIdsByProjectId(context, projectDTO.getId());
                channelIds.forEach(ch -> dmMessageSenderService.sendMessageToSubscribedChannel(ch, fileCommentWebhookResponse));
            }
        }
    }
}
