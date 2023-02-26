package com.mattermost.integration.figma.input.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class App {

        @JsonProperty("SchemaVersion")
        private String schemaVersion;
        @JsonProperty("app_id")
        private String appId;
        @JsonProperty("version")
        private String version;
        @JsonProperty("homepage_url")
        private String homepageUrl;
        @JsonProperty("deploy_type")
        private String deployType;
        @JsonProperty("webhook_secret")
        private String webhookSecret;
        @JsonProperty("bot_user_id")
        private String botUserId;
        @JsonProperty("bot_username")
        private String botUsername;
        @JsonProperty("remote_oauth2")
        private RemoteOauth2 remoteOauth2;

}
