package com.mattermost.integration.figma.api.figma.team;

import com.mattermost.integration.figma.api.figma.team.dto.TeamNameDto;

import java.util.List;

public interface TeamNameService {
    List<TeamNameDto> getAllTeamNames(String userId, String mmSiteUrl, String botAccessToken);
}
