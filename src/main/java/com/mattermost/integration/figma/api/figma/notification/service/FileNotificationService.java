package com.mattermost.integration.figma.api.figma.notification.service;

import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.input.oauth.InputPayload;

public interface FileNotificationService {
    SubscribeToFileNotification subscribeToFileNotification(InputPayload inputPayload);
    void sendFileNotificationMessageToMM(FileCommentWebhookResponse fileCommentWebhookResponse);
    void sendFileNotificationMessageToMMSubscribedChannels(FileCommentWebhookResponse fileCommentWebhookResponse);
}
