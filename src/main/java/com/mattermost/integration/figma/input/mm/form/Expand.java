package com.mattermost.integration.figma.input.mm.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Expand {
    @JsonProperty("acting_user_access_token")
    private String actingUserAccessToken;
    private String app;
    @JsonProperty("oauth2_app")
    private String oauth2App;
    @JsonProperty("oauth2_user")
    private String oauth2User;
    private String channel;
}
