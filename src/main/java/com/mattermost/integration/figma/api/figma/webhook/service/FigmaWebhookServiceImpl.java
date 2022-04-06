package com.mattermost.integration.figma.api.figma.webhook.service;

import com.mattermost.integration.figma.api.figma.webhook.dto.TeamWebhookInfoResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FigmaWebhookServiceImpl implements FigmaWebhookService {
    private static final String FIGMA_WEBHOOK_URL = "https://api.figma.com/v2/teams/";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public TeamWebhookInfoResponseDto getTeamWebhooks(String teamId, String figmaToken) {
        String url = String.format("%s%s/webhooks", FIGMA_WEBHOOK_URL, teamId);

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", figmaToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<TeamWebhookInfoResponseDto> resp = restTemplate.exchange(url, HttpMethod.GET, request, TeamWebhookInfoResponseDto.class);
        return resp.getBody();
    }
}
