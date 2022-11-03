package com.mattermost.integration.figma.api.figma.project.service;

import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.api.figma.webhook.service.FigmaWebhookService;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.input.figma.notification.FigmaWebhookResponse;
import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.UserDataDto;
import com.mattermost.integration.figma.security.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
public class FigmaProjectServiceImpl implements FigmaProjectService {

    private final String PROJECT_BY_TEAM_URL = "https://api.figma.com/v1/teams/%s/projects";

    @Autowired
    @Qualifier("figmaRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate defaultRestTemplate;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserDataKVService userDataKVService;

    @Autowired
    private FigmaWebhookService figmaWebhookService;


    @Override
    public Optional<TeamProjectDTO> getProjectsByTeamId(FileCommentWebhookResponse response) {
        FigmaWebhookResponse figmaData = response.getValues().getData();
        String botAccessToken = response.getContext().getBotAccessToken();

        Context context = response.getContext();
        String mattermostSiteUrl = context.getMattermostSiteUrl();
        Optional<String> commenterTeamId = figmaWebhookService.getCurrentUserTeamId(figmaData.getWebhookId(),
                mattermostSiteUrl, botAccessToken);

        if (commenterTeamId.isEmpty()) {
            log.info(String.format("Team id for %s webhook was not found", figmaData.getWebhookId()));
            return Optional.empty();
        }

        String commenterId = figmaData.getTriggeredBy().getId();
        return getProjectsByTeamId(commenterTeamId.get(), commenterId, mattermostSiteUrl, botAccessToken);
    }

    @Override
    public TeamProjectDTO getProjectsByTeamId(InputPayload inputPayload) {
        String teamId = inputPayload.getValues().getTeamId();
        String refreshToken = inputPayload.getContext().getOauth2().getUser().getRefreshToken();
        String clientId = inputPayload.getContext().getOauth2().getClientId();
        String clientSecret = inputPayload.getContext().getOauth2().getClientSecret();

        String accessToken = oAuthService.refreshToken(clientId, clientSecret, refreshToken).getAccessToken();

        return sendGetProjectRequest(teamId, accessToken);
    }


    @Override
    public Optional<TeamProjectDTO> getProjectsByTeamId(String teamId, String figmaUserId, String mmSiteUrl, String botAccessToken) {
        Optional<UserDataDto> userDataOptional = userDataKVService.getUserData(figmaUserId, mmSiteUrl, botAccessToken);

        if (userDataOptional.isEmpty()) {
            return Optional.empty();
        }
        UserDataDto userData = userDataOptional.get();
        String accessToken = oAuthService.refreshToken(userData.getClientId(), userData.getClientSecret(),
                userData.getRefreshToken()).getAccessToken();
        return Optional.of(sendGetProjectRequest(teamId, accessToken));
    }

    public Optional<TeamProjectDTO> getProjectsByTeamIdWithCustomRestTemplate(String teamId, String figmaUserId, String mmSiteUrl, String botAccessToken) {
        Optional<UserDataDto> userDataOptional = userDataKVService.getUserData(figmaUserId, mmSiteUrl, botAccessToken);

        if (userDataOptional.isEmpty()) {
            return Optional.empty();
        }
        UserDataDto userData = userDataOptional.get();
        String accessToken = oAuthService.refreshToken(userData.getClientId(), userData.getClientSecret(),
                userData.getRefreshToken()).getAccessToken();
        return getProjectsByTeamIdWithCustomRestTemplate(teamId, accessToken);
    }

    private TeamProjectDTO sendGetProjectRequest(String teamId, String accessToken) {
        String url = String.format(PROJECT_BY_TEAM_URL, teamId);
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", accessToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<TeamProjectDTO> resp = restTemplate.exchange(url, HttpMethod.GET, request, TeamProjectDTO.class);
        return resp.getBody();
    }

    public Optional<TeamProjectDTO> getProjectsByTeamIdWithCustomRestTemplate(String teamId, String accessToken) {

        String url = String.format(PROJECT_BY_TEAM_URL, teamId);
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", accessToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<TeamProjectDTO> resp = defaultRestTemplate.exchange(url, HttpMethod.GET, request, TeamProjectDTO.class);
            return Optional.of(resp.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }
}
