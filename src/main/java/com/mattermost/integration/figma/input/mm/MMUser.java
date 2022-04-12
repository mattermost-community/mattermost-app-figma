package com.mattermost.integration.figma.input.mm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MMUser {
    public String id;
    @JsonProperty("create_at")
    public Long createAt;
    @JsonProperty("update_at")
    public Long updateAt;
    @JsonProperty("delete_at")
    public Long deleteAt;
    public String username;
    @JsonProperty("auth_data")
    public String authData;
    @JsonProperty("auth_service")
    public String authService;
    public String email;
    public String nickname;
    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("last_name")
    public String lastName;
    public String position;
    public String roles;
    public String locale;
    public Timezone timezone;
    @JsonProperty("disable_welcome_email")
    public boolean disableWelcomeEmail;
}
