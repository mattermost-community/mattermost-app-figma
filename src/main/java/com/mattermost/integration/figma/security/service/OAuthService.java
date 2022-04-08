package com.mattermost.integration.figma.security.service;

import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.dto.FigmaTokenDTO;

public interface OAuthService {

    String generateUrl(InputPayload payload);

    String getConnectUrl(InputPayload payload);

    void storeOAuthCreds(InputPayload payload);

    FigmaTokenDTO getFigmaUserToken(InputPayload payload);

    void storeFigmaUserToken(InputPayload payload, FigmaTokenDTO tokenDTO);

    FigmaOAuthRefreshTokenResponseDTO refreshToken(String clientId , String clientSecret , String refreshToken);
}
