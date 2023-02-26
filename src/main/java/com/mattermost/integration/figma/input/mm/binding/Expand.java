package com.mattermost.integration.figma.input.mm.binding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expand {
    @JsonProperty("acting_user_access_token")
    private String actingUserAccessToken;
    private String app;
    @JsonProperty("oauth2_app")
    private String oauth2App;
    @JsonProperty("oauth2_user")
    private String oauth2User;
    private String channel;
    @JsonProperty("acting_user")
    private String actingUser;
}
