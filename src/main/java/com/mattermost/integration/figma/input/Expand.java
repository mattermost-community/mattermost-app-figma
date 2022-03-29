package com.mattermost.integration.figma.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Expand {
    @JsonProperty("oauth2_app")
    private String oauth2App;
    @JsonProperty("acting_user_access_token")
    private String actingUserAccessToken;
}
