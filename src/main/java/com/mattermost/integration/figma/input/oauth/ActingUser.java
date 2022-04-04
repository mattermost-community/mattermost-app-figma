package com.mattermost.integration.figma.input.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ActingUser{
    @JsonProperty("id")
    private String id;
    @JsonProperty("deleteAt")
    private int deleteAt;
    @JsonProperty("username")
    private String username;
    @JsonProperty("auth_service")
    private String authService;
    @JsonProperty("email")
    private String email;
    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("position")
    private String position;
    @JsonProperty("roles")
    private String roles;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("timezone")
    private Object timezone;
    @JsonProperty("disable_welcome_email")
    private boolean disableWelcomeEmail;
}
