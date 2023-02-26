package com.mattermost.integration.figma.api.figma.webhook.service;

import com.mattermost.integration.figma.api.figma.webhook.dto.TeamWebhookInfoResponseDto;
import com.mattermost.integration.figma.api.figma.webhook.dto.Webhook;

import java.util.Optional;

public interface FigmaWebhookService {
    TeamWebhookInfoResponseDto getTeamWebhooks(String teamId, String figmaToken);

    void deleteWebhook(String webhookId, String figmaToken);

    Optional<Webhook> getWebhookById(String webhookId, String figmaToken);

    Optional<String> getCurrentUserTeamId(String webhookId, String mmSiteUrl, String botAccessToken);
}
