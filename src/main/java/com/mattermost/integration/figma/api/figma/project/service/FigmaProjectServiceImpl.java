package com.mattermost.integration.figma.api.figma.project.service;

import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FigmaProjectServiceImpl implements FigmaProjectService {

    private String PROJECT_BY_TEAM_URL = "https://api.figma.com/v1/teams/%s/projects";

    @Autowired
    @Qualifier("figmaRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private OAuthService oAuthService;

    @Override
    public TeamProjectDTO getProjectsByTeamId(InputPayload inputPayload) {


        String teamId = inputPayload.getValues().getTeamId();
        String refreshToken = inputPayload.getContext().getOauth2().getUser().getRefreshToken();
        String clientId = inputPayload.getContext().getOauth2().getClientId();
        String clientSecret = inputPayload.getContext().getOauth2().getClientSecret();

        String accessToken = oAuthService.refreshToken(clientId, clientSecret, refreshToken).getAccessToken();

        String url = String.format(PROJECT_BY_TEAM_URL, teamId);

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", accessToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<TeamProjectDTO> resp = restTemplate.exchange(url, HttpMethod.GET, request, TeamProjectDTO.class);
        return resp.getBody();
    }
}
