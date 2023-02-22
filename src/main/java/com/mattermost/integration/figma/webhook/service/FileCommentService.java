package com.mattermost.integration.figma.webhook.service;

import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;

public interface FileCommentService {

    void updateName(FileCommentWebhookResponse response);
}
