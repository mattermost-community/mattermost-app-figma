package com.mattermost.integration.figma.api.figma.project.service;

import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.input.oauth.InputPayload;

public interface FigmaProjectService {

    TeamProjectDTO getProjectsByTeamId(InputPayload payload);
    TeamProjectDTO getProjectsByTeamId(String teamId, String figmaUserId, String mmSiteUrl, String botAccessToken);
}
