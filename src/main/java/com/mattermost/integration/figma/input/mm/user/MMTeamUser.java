package com.mattermost.integration.figma.input.mm.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MMTeamUser {
    @JsonProperty("team_id")
    private String teamID;
    @JsonProperty("user_id")
    private String userID;
    @JsonProperty("roles")
    private String roles;
    @JsonProperty("delete_at")
    private long deleteAt;
    @JsonProperty("scheme_guest")
    private boolean schemeGuest;
    @JsonProperty("scheme_user")
    private boolean schemeUser;
    @JsonProperty("scheme_admin")
    private boolean schemeAdmin;
    @JsonProperty("explicit_roles")
    private String explicitRoles;
}
