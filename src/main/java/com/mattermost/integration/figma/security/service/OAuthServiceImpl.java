package com.mattermost.integration.figma.security.service;

import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuthServiceImpl implements OAuthService {

    @Autowired
    private JsonUtils jsonUtils;

    private static final String BASE_URL =  "https://www.figma.com/oauth?";


    @Override
    public String generateUrl(String params) {
        String clientId = jsonUtils.getJsonValue(params,"client_id" ).get();
        String redirect = jsonUtils.getJsonValue(params, "complete_url").get();
        String state = jsonUtils.getJsonValue(params, "state").get();
        String url = String.format("%sclient_id=%s&redirect_uri=%s&scope=file_read&state=%s&response_type=code",BASE_URL,clientId,redirect,state);
        return url;
    }

    @Override
    public String getConnectUrl(String params) {
        return jsonUtils.getJsonValue(params,"connect_url" ).get();
    }
}