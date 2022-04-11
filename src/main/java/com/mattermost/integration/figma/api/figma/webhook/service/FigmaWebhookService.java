package com.mattermost.integration.figma.api.figma.webhook.service;

import com.mattermost.integration.figma.api.figma.webhook.dto.TeamWebhookInfoResponseDto;

public interface FigmaWebhookService {

    TeamWebhookInfoResponseDto getTeamWebhooks(String teamId, String figmaToken);
    void deleteWebhook(String webhookId, String figmaToken);
}
