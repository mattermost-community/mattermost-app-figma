package com.mattermost.integration.figma.api.figma.webhook.service;

import com.mattermost.integration.figma.api.figma.webhook.dto.TeamWebhookInfoResponseDto;
import com.mattermost.integration.figma.api.figma.webhook.dto.Webhook;
import com.mattermost.integration.figma.api.mm.kv.KVService;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.dto.UserDataDto;
import com.mattermost.integration.figma.security.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FigmaWebhookServiceImpl implements FigmaWebhookService {
    private static final String FIGMA_TEAM_WEBHOOKS_URL = "https://api.figma.com/v2/teams/";
    private static final String FIGMA_WEBHOOK_URL = "https://api.figma.com/v2/webhooks";

    @Autowired
    @Qualifier("figmaRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private KVService kvService;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserDataKVService userDataKVService;

    @Override
    public TeamWebhookInfoResponseDto getTeamWebhooks(String teamId, String figmaToken) {
        String url = String.format("%s%s/webhooks", FIGMA_TEAM_WEBHOOKS_URL, teamId);

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", figmaToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<TeamWebhookInfoResponseDto> resp = restTemplate.exchange(url, HttpMethod.GET, request, TeamWebhookInfoResponseDto.class);
        return resp.getBody();
    }

    @Override
    public void deleteWebhook(String webhookId, String figmaToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", String.format("Bearer %s", figmaToken));

        HttpEntity<Object> request = new HttpEntity<>(headers);
        String url = FIGMA_WEBHOOK_URL.concat("/").concat(webhookId);

        restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        log.info("Successfully deleted webhook with id: " + webhookId);
    }

    @Override
    public Webhook getWebhookById(String webhookId, String figmaToken) {
        String url = FIGMA_WEBHOOK_URL.concat("/").concat(webhookId);

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", figmaToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<Webhook> resp = restTemplate.exchange(url, HttpMethod.GET, request, Webhook.class);
        return resp.getBody();
    }

    @Override
    public String getCurrentUserTeamId(String webhookId, String mmSiteUrl, String botAccessToken) {
        String webhookOwnerId = kvService.get(webhookId, mmSiteUrl, botAccessToken);
        String accessToken = getToken(userDataKVService.getUserData(webhookOwnerId, mmSiteUrl, botAccessToken));
        return getWebhookById(webhookId, accessToken).getTeamId();
    }

    private String getToken(UserDataDto userDataDto) {
        String refreshToken = userDataDto.getRefreshToken();
        String clientId = userDataDto.getClientId();
        String clientSecret = userDataDto.getClientSecret();

        FigmaOAuthRefreshTokenResponseDTO refreshTokenDTO = oAuthService.refreshToken(clientId, clientSecret, refreshToken);
        return refreshTokenDTO.getAccessToken();
    }
}
