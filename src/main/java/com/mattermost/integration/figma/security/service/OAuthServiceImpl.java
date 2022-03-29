package com.mattermost.integration.figma.security.service;

import com.mattermost.integration.figma.input.InputPayload;
import com.mattermost.integration.figma.security.dto.OAuthCredsDTO;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthServiceImpl implements OAuthService {
    private static final String STORE_CREDS_URL = "/plugins/com.mattermost.apps/api/v1/oauth2/app";
    private static final String BASE_URL = "https://www.figma.com/oauth?";
    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public String generateUrl(String params) {
        String clientId = jsonUtils.getJsonValue(params, "client_id").get();
        String redirect = jsonUtils.getJsonValue(params, "complete_url").get();
        String state = jsonUtils.getJsonValue(params, "state").get();
        String url = String.format("%sclient_id=%s&redirect_uri=%s&scope=file_read&state=%s&response_type=code", BASE_URL, clientId, redirect, state);
        return url;
    }

    @Override
    public String getConnectUrl(String params) {
        return jsonUtils.getJsonValue(params, "connect_url").get();
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
}