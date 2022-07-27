package com.mattermost.integration.figma.input.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mattermost.integration.figma.input.mm.form.Field;
import com.mattermost.integration.figma.input.mm.form.MMStaticSelectField;
import lombok.Data;

@Data
public class Values{
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("code")
    private String code;
    @JsonProperty("state")
    private String state;
    @JsonProperty("team_id")
    private String teamId;
    @JsonProperty("message")
    private String message;
    @JsonProperty("project_id")
    private MMStaticSelectField project;
    @JsonProperty("file_id")
    private MMStaticSelectField file;
    @JsonProperty("team_name")
    private MMStaticSelectField teamName;
}
