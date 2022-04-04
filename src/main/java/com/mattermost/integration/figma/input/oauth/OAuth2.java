package com.mattermost.integration.figma.input.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OAuth2 {
    @JsonProperty("connect_url")
    private String connectUrl;
    @JsonProperty("complete_url")
    private String completeUrl;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("user")
    private User user;
}