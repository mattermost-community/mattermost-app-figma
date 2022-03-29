package com.mattermost.integration.figma.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OAuth2 {
    @JsonProperty("connect_url")
    private String connectUrl;
    @JsonProperty("complete_url")
    private String completeUrl;
}