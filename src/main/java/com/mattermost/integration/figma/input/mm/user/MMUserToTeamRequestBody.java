package com.mattermost.integration.figma.input.mm.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MMUserToTeamRequestBody {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("team_id")
    private String teamId;
}
