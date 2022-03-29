package com.mattermost.integration.figma.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Values{
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
}
