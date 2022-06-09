package com.mattermost.integration.figma.api.mm.dm.service;

import com.mattermost.integration.figma.api.mm.dm.dto.DMMessagePayload;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.kv.dto.ProjectInfo;
import com.mattermost.integration.figma.input.figma.notification.FigmaWebhookResponse;
import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.UserDataDto;

import java.util.Optional;

public interface DMMessageSenderService {
    String sendMessageToCommentAuthor(FigmaWebhookResponse figmaWebhookResponse, Context context, String fileOwnerId);

    void sendMessageToSpecificReceiver(Context context, Optional<UserDataDto> specificUserData,
                                       FigmaWebhookResponse figmaWebhookResponse, String notificationMessageRoot);

    String sendMessageToFileOwner(FigmaWebhookResponse figmaWebhookResponse, Context context);

    void sendFileSubscriptionToMMChat(FileInfo file , InputPayload payload);

    void sendMessageToSubscribedChannel(String channelId, FileCommentWebhookResponse fileCommentWebhookResponse);

    void sendMessage(InputPayload payload , String text);

    void sendProjectSubscriptionsToMMChat(ProjectInfo project, InputPayload payload);
}
