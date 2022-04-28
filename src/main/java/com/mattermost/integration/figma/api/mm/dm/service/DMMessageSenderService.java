package com.mattermost.integration.figma.api.mm.dm.service;

import com.mattermost.integration.figma.input.figma.notification.FigmaWebhookResponse;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.security.dto.UserDataDto;

public interface DMMessageSenderService {
    String sendMessageToCommentAuthor(FigmaWebhookResponse figmaWebhookResponse, Context context, String fileOwnerId);

    void sendMessageToSpecificReceiver(Context context, UserDataDto specificUserData,
                                       FigmaWebhookResponse figmaWebhookResponse, String notificationMessageRoot);

    String sendMessageToFileOwner(FigmaWebhookResponse figmaWebhookResponse, Context context);
}
