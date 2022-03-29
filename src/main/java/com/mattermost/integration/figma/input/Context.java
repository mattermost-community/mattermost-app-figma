package com.mattermost.integration.figma.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Context{
    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("location")
    private String location;
    @JsonProperty("user_agent")
    private String userAgent;
    @JsonProperty("track_as_submit")
    private boolean trackAsSubmit;
    @JsonProperty("mattermost_site_url")
    private String mattermostSiteUrl;
    @JsonProperty("developer_mode")
    private boolean developerMode;
    @JsonProperty("app_path")
    private String appPath;
    @JsonProperty("bot_user_id")
    private String botUserId;
    @JsonProperty("bot_access_token")
    private String botAccessToken;
    @JsonProperty("acting_user")
    private ActingUser actingUser;
    @JsonProperty("oauth2")
    private OAuth2 oauth2;
    @JsonProperty("acting_user_access_token")
    private String actingUserAccessToken;

}
