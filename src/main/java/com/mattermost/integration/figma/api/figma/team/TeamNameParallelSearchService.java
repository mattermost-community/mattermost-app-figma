package com.mattermost.integration.figma.api.figma.team;

import com.mattermost.integration.figma.api.figma.team.dto.TeamNameDto;

import java.util.List;

public interface TeamNameParallelSearchService {
    List<TeamNameDto> doTeamNameSearchTask(List<TeamNameDto> teamNameDtos, String userId,
                                           String mmSiteUrl, String botAccessToken);
}
