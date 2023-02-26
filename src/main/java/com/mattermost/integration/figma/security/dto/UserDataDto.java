package com.mattermost.integration.figma.security.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDataDto {
    private Set<String> teamIds;
    private String mmUserId;
    private String refreshToken;
    private String clientSecret;
    private String clientId;
    private boolean isConnected;
}
