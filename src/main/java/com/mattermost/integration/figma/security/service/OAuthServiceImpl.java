package com.mattermost.integration.figma.security.service;

import com.mattermost.integration.figma.input.InputPayload;
import com.mattermost.integration.figma.security.dto.FigmaTokenDTO;
import com.mattermost.integration.figma.security.dto.OAuthCredsDTO;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthServiceImpl implements OAuthService {
    private static final String BASE_URL = "https://www.figma.com";
    private static final String BASE_PLUGIN_URL = "/plugins/com.mattermost.apps/api/v1/";
    private static final String STORE_USER_OAUTH2_URL = BASE_PLUGIN_URL +"oauth2/user";
    private static final String STORE_CREDS_URL =  BASE_PLUGIN_URL+"oauth2/app";

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private RestTemplate restTemplate;


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
        return  payload.getContext().getOauth2().getConnectUrl();
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
        ResponseEntity<String> resp = restTemplate.postForEntity(String.format("%s%s", mmSiteUrlBase, STORE_CREDS_URL), request, String.class);
    }

    @Override
    public FigmaTokenDTO getFigmaUserToken(InputPayload payload) {
        String redirectUrl = payload.getContext().getOauth2().getCompleteUrl();
        String clientId = payload.getContext().getOauth2().getClientId();
        String clientSecret = payload.getContext().getOauth2().getClientSecret();
        String code = payload.getValues().getCode();
        String url = String.format("%s/api/oauth/token?client_id=%s&client_secret=%s&redirect_uri=%s&code=%s&grant_type=authorization_code", BASE_URL,clientId, clientSecret, redirectUrl, code);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, null, String.class);
        FigmaTokenDTO token = (FigmaTokenDTO) jsonUtils.convertStringToObject(resp.getBody(),FigmaTokenDTO.class).get();
        return token;
    }

    @Override
    public void storeFigmaUserToken(InputPayload payload, FigmaTokenDTO tokenDTO) {
        String url = String.format("%s%s",payload.getContext().getMattermostSiteUrl(),STORE_USER_OAUTH2_URL);
        String actingUserToken = payload.getContext().getActingUserAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("Bearer %s", actingUserToken));

        HttpEntity<OAuthCredsDTO> request = new HttpEntity(tokenDTO, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity(url, request, String.class);
    }
}

