package com.mattermost.integration.figma.input.mm.manifest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Manifest {

    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("version")
    private String version;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("homepage_url")
    private String homepageUrl;
    @JsonProperty("requested_permissions")
    private List<String> requestedPermissions;
    @JsonProperty("requested_locations")
    private List<String> requestedLocations;
    @JsonProperty("bindings")
    private Bindings bindings;
    @JsonProperty("get_oauth2_connect_url")
    private LifecycleURL getOauth2ConnectUrl;
    @JsonProperty("on_oauth2_complete")
    private LifecycleURL onOauth2Complete;
    @JsonProperty("http")
    private Http http;
    @JsonProperty("aws_lambda")
    private AwsLambda awsLambda;
    @JsonProperty("remote_webhook_auth_type")
    private String webhookAuthType;
    @JsonProperty("assets")
    private List<Object> assets;
    @JsonProperty("on_uninstall")
    private LifecycleURL onUninstall;
}
