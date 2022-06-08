package com.mattermost.integration.figma.input.mm.manifest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Expand {
    @JsonProperty("acting_user")
    private String actingUser;
    @JsonProperty("oauth2_app")
    private String oauth2App;
    @JsonProperty("oauth2_user")
    private String oauth2User;
    @JsonProperty("acting_user_access_token")
    private String actingUserAccessToken;
}
