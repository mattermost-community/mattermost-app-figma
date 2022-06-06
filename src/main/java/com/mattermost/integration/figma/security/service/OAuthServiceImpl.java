package com.mattermost.integration.figma.security.service;

import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.input.oauth.User;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.dto.FigmaTokenDTO;
import com.mattermost.integration.figma.security.dto.OAuthCredsDTO;
import com.mattermost.integration.figma.security.dto.UserDataDto;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OAuthServiceImpl implements OAuthService {
    private static final String BASE_URL = "https://www.figma.com";
    private static final String BASE_PLUGIN_URL = "/plugins/com.mattermost.apps/api/v1/";
    private static final String STORE_USER_OAUTH2_URL = BASE_PLUGIN_URL + "oauth2/user";
    private static final String STORE_CREDS_URL = BASE_PLUGIN_URL + "oauth2/app";
    private static final String REFRESH_TOKEN_URL = BASE_URL + "/api/oauth/refresh";

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    @Qualifier("figmaRestTemplate")
    private RestTemplate figmaRestTemplate;

    @Autowired
    @Qualifier("mmRestTemplate")
    private RestTemplate mmRestTemplate;

    @Autowired
    private UserDataKVService userDataKVService;

    @Override
    public String generateUrl(InputPayload payload) {
        String clientId = payload.getContext().getOauth2().getClientId();
        String redirect = payload.getContext().getOauth2().getCompleteUrl();
        String state = payload.getValues().getState();
        String url = String.format("%s/oauth?client_id=%s&redirect_uri=%s&scope=file_read&state=%s&response_type=code", BASE_URL, clientId, redirect, state);
        return url;
    }

    @Override
    public String getConnectUrl(InputPayload payload) {
        return payload.getContext().getOauth2().getConnectUrl();
    }

    @Override
    public void storeOAuthCreds(InputPayload payload) {

        String clientId = payload.getValues().getClientId();
        String clientSecret = payload.getValues().getClientSecret();
        String appId = payload.getContext().getAppId();
        String mmSiteUrlBase = payload.getContext().getMattermostSiteUrl();
        String actingUserToken = payload.getContext().getActingUserAccessToken();

        OAuthCredsDTO credsDTO = new OAuthCredsDTO(clientId, clientSecret, appId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("Bearer %s", actingUserToken));

        HttpEntity<OAuthCredsDTO> request = new HttpEntity<>(credsDTO, headers);
        log.info("Sending request to store OauthCreds for client with id: " + clientId);
        mmRestTemplate.postForEntity(String.format("%s%s", mmSiteUrlBase, STORE_CREDS_URL), request, String.class);
        log.info("Successfully stored creds");
    }

    @Override
    public FigmaTokenDTO getFigmaUserToken(InputPayload payload) {
        String redirectUrl = payload.getContext().getOauth2().getCompleteUrl();
        String clientId = payload.getContext().getOauth2().getClientId();
        String clientSecret = payload.getContext().getOauth2().getClientSecret();
        String code = payload.getValues().getCode();
        String url = String.format("%s/api/oauth/token?client_id=%s&client_secret=%s&redirect_uri=%s&code=%s&grant_type=authorization_code", BASE_URL, clientId, clientSecret, redirectUrl, code);
        log.info("Sending request to get figma token for client with id " + clientId);
        ResponseEntity<String> resp = figmaRestTemplate.postForEntity(url, null, String.class);
        FigmaTokenDTO token = (FigmaTokenDTO) jsonUtils.convertStringToObject(resp.getBody(), FigmaTokenDTO.class).get();
        log.info("Successfully received token");
        return token;
    }

    @Override
    public void storeFigmaUserToken(InputPayload payload, FigmaTokenDTO tokenDTO) {
        String url = String.format("%s%s", payload.getContext().getMattermostSiteUrl(), STORE_USER_OAUTH2_URL);
        String actingUserToken = payload.getContext().getActingUserAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("Bearer %s", actingUserToken));

        HttpEntity<OAuthCredsDTO> request = new HttpEntity(tokenDTO, headers);
        log.info("Sending request to store figmaUserToken for client with id: " + payload.getContext().getOauth2()
                .getClientId());
        mmRestTemplate.postForEntity(url, request, String.class);
        payload.getContext().getOauth2().setUser(new User(null, 0, tokenDTO.getRefreshToken(), tokenDTO.getUserId()));
        userDataKVService.storePrimaryUserData(payload, new UserDataDto());
        userDataKVService.saveNewUserToAllUserIdsSet(tokenDTO.getUserId(), payload.getContext().getMattermostSiteUrl(),
                payload.getContext().getBotAccessToken());
        log.info("Successfully stored token");
    }

    @Override
    public FigmaOAuthRefreshTokenResponseDTO refreshToken(String clientId, String clientSecret, String refreshToken) {
        String url = String.format("%s?client_id=%s&client_secret=%s&refresh_token=%s", REFRESH_TOKEN_URL, clientId, clientSecret, refreshToken);

        ResponseEntity<String> resp = figmaRestTemplate.exchange(url, HttpMethod.POST, ResponseEntity.EMPTY, String.class);

        return (FigmaOAuthRefreshTokenResponseDTO) jsonUtils.convertStringToObject(resp.getBody(), FigmaOAuthRefreshTokenResponseDTO.class).get();
    }
}

