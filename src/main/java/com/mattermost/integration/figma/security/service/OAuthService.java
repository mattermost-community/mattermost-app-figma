package com.mattermost.integration.figma.security.service;

public interface OAuthService {

    String generateUrl(String params);
    String getConnectUrl(String params);
}
