package com.mattermost.integration.figma.api.figma.webhook.service;

import com.mattermost.integration.figma.api.figma.webhook.dto.TeamWebhookInfoResponseDto;
import com.mattermost.integration.figma.api.figma.webhook.dto.Webhook;

public interface FigmaWebhookService {
    TeamWebhookInfoResponseDto getTeamWebhooks(String teamId, String figmaToken);

    void deleteWebhook(String webhookId, String figmaToken);

    Webhook getWebhookById(String webhookId, String figmaToken);

    String getCurrentUserTeamId(String webhookId, String mmSiteUrl, String botAccessToken);
}
