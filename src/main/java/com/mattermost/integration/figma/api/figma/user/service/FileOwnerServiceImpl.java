package com.mattermost.integration.figma.api.figma.user.service;

import com.mattermost.integration.figma.api.figma.user.service.dto.FileOwnerResponseDto;
import com.mattermost.integration.figma.api.figma.webhook.service.FigmaWebhookService;
import com.mattermost.integration.figma.api.mm.kv.KVService;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.dto.UserDataDto;
import com.mattermost.integration.figma.security.service.OAuthService;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Service
public class FileOwnerServiceImpl implements FileOwnerService {

    private static final String FIGMA_FILE_URL = "https://api.figma.com/v1/files/%s";
    private static final String OWNER = "owner";

    private final UserDataKVService userDataKVService;
    private final RestTemplate restTemplate;
    private final JsonUtils jsonUtils;
    private final OAuthService oAuthService;
    private final FigmaWebhookService figmaWebhookService;
    private final KVService kvService;

    public FileOwnerServiceImpl(UserDataKVService userDataKVService, RestTemplate restTemplate, JsonUtils jsonUtils, OAuthService oAuthService, FigmaWebhookService figmaWebhookService, KVService kvService) {
        this.userDataKVService = userDataKVService;
        this.restTemplate = restTemplate;
        this.jsonUtils = jsonUtils;
        this.oAuthService = oAuthService;
        this.figmaWebhookService = figmaWebhookService;
        this.kvService = kvService;
    }

    @Override
    public String findFileOwnerId(String fileKey, String webhookId, String figmaUserId, String mmSiteUrl, String botAccessToken) {
        String teamId = getCurrentUserTeamId(webhookId, mmSiteUrl, botAccessToken);
        Set<String> userIds = userDataKVService.getUserIdsByTeamId(teamId, mmSiteUrl, botAccessToken);
        for (String userId : userIds) {
            UserDataDto currentUserData = userDataKVService.getUserData(userId, mmSiteUrl, botAccessToken);
            if (checkIfUserIsOwner(getToken(currentUserData), fileKey)) {
                return userId;
            }
        }
        return null;
    }

    private boolean checkIfUserIsOwner(String figmaAccessToken, String fileKey) {
        FileOwnerResponseDto fileOwnerResponseDto = sendGetFileRequest(fileKey, figmaAccessToken);
        return OWNER.equals(fileOwnerResponseDto.getRole());
    }

    private FileOwnerResponseDto sendGetFileRequest(String fileKey, String figmaAccessToken) {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", figmaAccessToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(String.format(FIGMA_FILE_URL, fileKey), HttpMethod.GET, request, String.class);

        return (FileOwnerResponseDto) jsonUtils.convertStringToObject(resp.getBody(), FileOwnerResponseDto.class).get();
    }

    private String getToken(UserDataDto userDataDto) {
        String refreshToken = userDataDto.getRefreshToken();
        String clientId = userDataDto.getClientId();
        String clientSecret = userDataDto.getClientSecret();

        FigmaOAuthRefreshTokenResponseDTO refreshTokenDTO = oAuthService.refreshToken(clientId, clientSecret, refreshToken);
        return refreshTokenDTO.getAccessToken();
    }

    private String getCurrentUserTeamId(String webhookId, String mmSiteUrl, String botAccessToken) {
        String webhookOwnerId = kvService.get(webhookId, mmSiteUrl, botAccessToken);
        String accessToken = getToken(userDataKVService.getUserData(webhookOwnerId, mmSiteUrl, botAccessToken));
        return figmaWebhookService.getWebhookById(webhookId, accessToken).getTeamId();
    }

}
