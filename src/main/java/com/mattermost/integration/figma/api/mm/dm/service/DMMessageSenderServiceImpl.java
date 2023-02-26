package com.mattermost.integration.figma.api.mm.dm.service;

import com.mattermost.integration.figma.api.figma.comment.service.CommentService;
import com.mattermost.integration.figma.api.figma.user.service.FileOwnerService;
import com.mattermost.integration.figma.api.mm.dm.component.DMCallButtonMessageCreator;
import com.mattermost.integration.figma.api.mm.dm.component.DMFormMessageCreator;
import com.mattermost.integration.figma.api.mm.dm.dto.*;
import com.mattermost.integration.figma.api.mm.kv.KVService;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.kv.dto.ProjectInfo;
import com.mattermost.integration.figma.api.mm.server.ServerConfigurationService;
import com.mattermost.integration.figma.api.mm.user.MMUserService;
import com.mattermost.integration.figma.input.figma.notification.*;
import com.mattermost.integration.figma.input.mm.user.MMUser;
import com.mattermost.integration.figma.input.oauth.ActingUser;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.dto.UserDataDto;
import com.mattermost.integration.figma.security.service.OAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.mattermost.integration.figma.constant.prefixes.webhook.TeamWebhookPrefixes.WEBHOOK_ID_PREFIX;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class DMMessageSenderServiceImpl implements DMMessageSenderService {

    private static final String FILE_URL = "https://www.figma.com/file/%s";
    private static final String UNTITLED = "Untitled";
    private static final String REPLY_NOTIFICATION_ROOT = "figma.webhook.reply.notification.root.label";
    private static final String AUTHOR_ID_MATCHED_COMMENTER_ID = "";
    private static final String FILE_OWNER_ID_MATCHED_COMMENTER_ID = "";
    private static final String COMMENTED_IN_YOUR_FILE = "figma.webhook.reply.notification.commented.in.your.file.label";
    private static final String COMMENTED_IN_FILE = "figma.webhook.reply.notification.commented.in.file.label";

    @Autowired
    private MMUserService mmUserService;
    @Autowired
    private DMFormMessageCreator formMessageCreator;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserDataKVService userDataKVService;
    @Autowired
    private DMMessageService messageService;
    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private FileOwnerService fileOwnerService;
    @Autowired
    private DMCallButtonMessageCreator messageCreator;
    @Autowired
    private KVService kvService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ServerConfigurationService serverConfigurationService;


    public String sendMessageToCommentAuthor(FigmaWebhookResponse figmaWebhookResponse, Context context, String fileOwnerId) {
        String token = getAccessTokenByWebhookId(figmaWebhookResponse.getWebhookId(), context.getMattermostSiteUrl(), context.getBotAccessToken());
        if (StringUtils.isBlank(token)) {
            return token;
        }
        CommentDto comment = commentService.getCommentById(figmaWebhookResponse.getParentId(),
                figmaWebhookResponse.getFileKey(), token).get();
        if (figmaWebhookResponse.getTriggeredBy().getId().equals(comment.getUser().getId())) {
            return AUTHOR_ID_MATCHED_COMMENTER_ID;
        }

        if (!fileOwnerId.equals(comment.getUser().getId())) {
            Optional<UserDataDto> currentUserData = userDataKVService.getUserData(comment.getUser().getId(),
                    context.getMattermostSiteUrl(), context.getBotAccessToken());

            if (currentUserData.isEmpty()) {
                return StringUtils.EMPTY;
            }

            UserDataDto userDataDto = currentUserData.get();
            if (userDataDto.isConnected()) {
                MMUser user = mmUserService.getUserById(userDataDto.getMmUserId(), context.getMattermostSiteUrl(), context.getBotAccessToken());
                Locale locale = Locale.forLanguageTag(user.getLocale());
                String message = messageSource.getMessage(REPLY_NOTIFICATION_ROOT, null, locale);
                sendMessageToSpecificReceiver(context, userDataDto, figmaWebhookResponse, message);
                return comment.getUser().getId();
            }
        }
        return fileOwnerId;
    }

    public String sendMessageToFileOwner(FigmaWebhookResponse figmaWebhookResponse, Context context) {
        String fileOwnerId = fileOwnerService.findFileOwnerId(figmaWebhookResponse.getFileKey(),
                figmaWebhookResponse.getWebhookId(), figmaWebhookResponse.getTriggeredBy().getId(),
                context.getMattermostSiteUrl(), context.getBotAccessToken());
        if (!figmaWebhookResponse.getTriggeredBy().getId().equals(fileOwnerId)) {
            Optional<UserDataDto> fileOwnerData = userDataKVService.getUserData(fileOwnerId, context.getMattermostSiteUrl(), context.getBotAccessToken());
            fileOwnerData.ifPresent(userDataDto -> sendMessageToSpecificReceiver(figmaWebhookResponse, context, userDataDto));
            return fileOwnerId;
        }
        return FILE_OWNER_ID_MATCHED_COMMENTER_ID;
    }

    private void sendMessageToSpecificReceiver(FigmaWebhookResponse figmaWebhookResponse, Context context, UserDataDto fileOwnerData) {
        if (fileOwnerData.isConnected()) {
            MMUser user = mmUserService.getUserById(fileOwnerData.getMmUserId(), context.getMattermostSiteUrl(), context.getBotAccessToken());
            Locale locale = Locale.forLanguageTag(user.getLocale());
            String message = messageSource.getMessage(COMMENTED_IN_YOUR_FILE, null, locale);
            sendMessageToSpecificReceiver(context, fileOwnerData, figmaWebhookResponse, message);
        }
    }

    @Override
    public void sendFileSubscriptionToMMChat(FileInfo file, InputPayload payload) {
        String userId = file.getUserId();
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String botAccessToken = payload.getContext().getBotAccessToken();

        MMUser user = mmUserService.getUserById(userId, mattermostSiteUrl, botAccessToken);

        FileSubscriptionMessage fileSubscriptionMessage = new FileSubscriptionMessage();
        fileSubscriptionMessage.setFileInfo(file);
        fileSubscriptionMessage.setPayload(payload);
        fileSubscriptionMessage.setMmUser(user);

        messageService.sendDMMessage(messageCreator.createDMMessageWithPropsPayload(fileSubscriptionMessage));
    }

    @Override
    public void sendProjectSubscriptionsToMMChat(ProjectInfo project, InputPayload payload) {
        String userId = project.getUserId();
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String botAccessToken = payload.getContext().getBotAccessToken();

        MMUser user = mmUserService.getUserById(userId, mattermostSiteUrl, botAccessToken);

        ProjectSubscriptionMessage projectSubscriptionMessage = new ProjectSubscriptionMessage();
        projectSubscriptionMessage.setProjectInfo(project);
        projectSubscriptionMessage.setPayload(payload);
        projectSubscriptionMessage.setMmUser(user);

        messageService.sendDMMessage(messageCreator.createDMMessageWithPropsPayload(projectSubscriptionMessage));
    }

    @Override
    public void sendMessageToSubscribedChannel(String channelId, FileCommentWebhookResponse webhookResponse) {
        String mattermostSiteUrl = webhookResponse.getContext().getMattermostSiteUrl();
        String botAccessToken = webhookResponse.getContext().getBotAccessToken();
        Locale serverLocale = serverConfigurationService.getServerLocale(mattermostSiteUrl, botAccessToken);
        String message = messageSource.getMessage(COMMENTED_IN_FILE, null, serverLocale);
        Optional<DMMessageWithPropsFields> messageWithPropsFields = getMessageWithPropsFields(webhookResponse.getContext(), webhookResponse.getValues().getData(),
                channelId, message);

        if (messageWithPropsFields.isEmpty()) {
            return;
        }

        DMMessageWithPropsPayload dmMessageWithPropsPayload = formMessageCreator.createMessageWithPropsPayload(messageWithPropsFields.get(), botAccessToken,
                mattermostSiteUrl, serverLocale);

        messageService.sendDMMessage(dmMessageWithPropsPayload);

    }

    @Override
    public void sendMessage(InputPayload payload, String message) {
        Context context = payload.getContext();
        DMMessagePayload messagePayload = new DMMessagePayload();
        messagePayload.setChannelId(context.getChannel().getId());
        messagePayload.setMessage(message);
        messagePayload.setToken(context.getBotAccessToken());
        messagePayload.setMmSiteUrlBase(context.getMattermostSiteUrl());
        messageService.sendDMMessage(messagePayload);
    }

    @Override
    public void sendMessageToSpecificReceiver(Context context, UserDataDto specificUserData,
                                              FigmaWebhookResponse figmaWebhookResponse, String notificationMessageRoot) {

        context.setActingUser(new ActingUser());
        context.getActingUser().setId(specificUserData.getMmUserId());
        String channelId = messageService.createDMChannel(createDMChannelPayload(context));

        Optional<DMMessageWithPropsFields> messageWithPropsFields = getMessageWithPropsFields(context, figmaWebhookResponse,
                channelId, notificationMessageRoot);
        if (messageWithPropsFields.isEmpty()) {
            return;
        }
        String botAccessToken = context.getBotAccessToken();
        String mattermostSiteUrl = context.getMattermostSiteUrl();
        MMUser user = mmUserService.getUserById(specificUserData.getMmUserId(), mattermostSiteUrl, botAccessToken);

        Locale locale = Locale.forLanguageTag(user.getLocale());
        messageService.sendDMMessage(formMessageCreator.createMessageWithPropsPayload(messageWithPropsFields.get(), botAccessToken,
                mattermostSiteUrl, locale));
    }

    private String getAccessTokenByWebhookId(String webhookId, String mmSiteUrl, String botAccessToken) {
        String userId = kvService.get(WEBHOOK_ID_PREFIX.concat(webhookId), mmSiteUrl, botAccessToken);
        Optional<UserDataDto> userDataDto = userDataKVService.getUserData(userId, mmSiteUrl, botAccessToken);
        if (userDataDto.isEmpty()) {
            return StringUtils.EMPTY;
        }

        FigmaOAuthRefreshTokenResponseDTO figmaOAuthRefreshTokenResponseDTO =
                oAuthService.refreshToken(userDataDto.get().getClientId(), userDataDto.get().getClientSecret(), userDataDto.get().getRefreshToken());
        return figmaOAuthRefreshTokenResponseDTO.getAccessToken();
    }

    private String getToken(FigmaWebhookResponse figmaWebhookResponse, Context context) {
        String mmSiteUrl = context.getMattermostSiteUrl();
        String botAccessToken = context.getBotAccessToken();
        Optional<UserDataDto> userDataDto = userDataKVService.getUserData(figmaWebhookResponse.getTriggeredBy().getId(),
                mmSiteUrl, botAccessToken);

        if (userDataDto.isEmpty()) {
            return StringUtils.EMPTY;
        }

        FigmaOAuthRefreshTokenResponseDTO figmaOAuthRefreshTokenResponseDTO =
                oAuthService.refreshToken(userDataDto.get().getClientId(), userDataDto.get().getClientSecret(), userDataDto.get().getRefreshToken());
        return figmaOAuthRefreshTokenResponseDTO.getAccessToken();
    }

    private Optional<DMMessageWithPropsFields> getMessageWithPropsFields(Context context, FigmaWebhookResponse figmaWebhookResponse,
                                                                         String channelId, String notificationMessageRoot) {
        String mmSiteUrl = context.getMattermostSiteUrl();
        String botAccessToken = context.getBotAccessToken();

        Optional<UserDataDto> userDataDto = userDataKVService.getUserData(figmaWebhookResponse.getTriggeredBy().getId(), mmSiteUrl, botAccessToken);

        String userName;
        if (userDataDto.isEmpty()) {
            userName = figmaWebhookResponse.getTriggeredBy().getHandle();
        } else {
            MMUser mmUsers = mmUserService.getUserById(userDataDto.get().getMmUserId(), mmSiteUrl, botAccessToken);
            userName = String.format("@%s", mmUsers.getUsername());
        }

        DMMessageWithPropsFields msg = new DMMessageWithPropsFields();
        msg.setAppId(context.getAppId());
        msg.setLabel(buildLabel(userName, figmaWebhookResponse.getFileName(),
                figmaWebhookResponse.getFileKey(), notificationMessageRoot));
        msg.setChannelId(channelId);
        msg.setDescription(buildComment(figmaWebhookResponse.getComment(),
                figmaWebhookResponse.getMentions(), mmSiteUrl, botAccessToken));
        msg.setReplyFileId(figmaWebhookResponse.getFileKey());
        msg.setReplyCommentId(isBlank(figmaWebhookResponse.getParentId()) ? figmaWebhookResponse.getCommentId() : figmaWebhookResponse.getParentId());
        return Optional.of(msg);
    }

    private String buildLabel(String author, String fileName, String fileKey, String root) {
        if (fileName.isBlank()) {
            fileName = UNTITLED;
        }
        return String.format("%s %s [%s](%s):", author, root, fileName, String.format(FILE_URL, fileKey));
    }

    private String buildComment(List<Comment> comments, List<Mention> mentions, String mmSiteUrl,
                                String botToken) {
        List<Optional<UserDataDto>> userData = mentions.stream().map(mention ->
                userDataKVService.getUserData(mention.getId(), mmSiteUrl, botToken)).collect(Collectors.toList());
        List<String> userIds = userData.stream().filter(Optional::isPresent).map(u -> u.get().getMmUserId()).collect(Collectors.toList());
        List<MMUser> mmUsers = new ArrayList<>();
        if (!userIds.isEmpty()) {
            mmUsers = getMMUsersByIds(userIds, mmSiteUrl, botToken);
        }

        StringBuilder stringBuilder = new StringBuilder();
        int mentionCounter = 0;
        for (Comment comment : comments) {
            if (Objects.nonNull(comment.getText())) {
                stringBuilder.append(comment.getText());
            } else {
                stringBuilder.append(prepareSingleMention(mentions.get(mentionCounter++), mmUsers, mmSiteUrl, botToken));
            }
        }

        return stringBuilder.toString();
    }

    private String prepareSingleMention(Mention currentMention, List<MMUser> mmUsers, String mmSiteUrl, String token) {
        Optional<UserDataDto> userData = userDataKVService.getUserData(currentMention.getId(), mmSiteUrl, token);
        if (userData.isPresent()) {
            UserDataDto userDataDto = userData.get();
            Optional<MMUser> mentionedMMUser = mmUsers.stream().filter(mmUser -> mmUser.getId().equals(userDataDto.getMmUserId())).findFirst();
            if (mentionedMMUser.isPresent()) {
                return "@".concat(mentionedMMUser.get().getUsername());
            }
        }

        return currentMention.getHandle();
    }

    private List<MMUser> getMMUsersByIds(List<String> ids, String mmSiteUrl, String botAccessToken) {
        return mmUserService.getUsersById(ids, mmSiteUrl, botAccessToken);
    }

    private DMChannelPayload createDMChannelPayload(Context context) {
        String botAccessToken = context.getBotAccessToken();
        String botUserId = context.getBotUserId();
        String userId = context.getActingUser().getId();
        String mattermostSiteUrl = context.getMattermostSiteUrl();
        return new DMChannelPayload(userId, botUserId, botAccessToken, mattermostSiteUrl);
    }

}
