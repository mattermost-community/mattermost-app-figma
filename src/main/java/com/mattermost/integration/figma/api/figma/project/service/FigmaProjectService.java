package com.mattermost.integration.figma.api.figma.project.service;

import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public interface FigmaProjectService {

    Optional<TeamProjectDTO> getProjectsByTeamId(FileCommentWebhookResponse response);
    TeamProjectDTO getProjectsByTeamId(InputPayload payload);
    Optional<TeamProjectDTO> getProjectsByTeamId(String teamId, String figmaUserId, String mmSiteUrl, String botAccessToken);
    Optional<TeamProjectDTO> getProjectsByTeamIdWithCustomRestTemplate(String teamId, String figmaUserId, String mmSiteUrl, String botAccessToken);
    Optional<TeamProjectDTO> getProjectsByTeamIdWithCustomRestTemplate(String teamId, String accessToken);
}
