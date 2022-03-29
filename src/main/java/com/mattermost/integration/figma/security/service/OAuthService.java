package com.mattermost.integration.figma.security.service;

import com.mattermost.integration.figma.input.InputPayload;

public interface OAuthService {

    String generateUrl(String params);
    String getConnectUrl(String params);
    void storeOAuthCreds(InputPayload payload);
}
