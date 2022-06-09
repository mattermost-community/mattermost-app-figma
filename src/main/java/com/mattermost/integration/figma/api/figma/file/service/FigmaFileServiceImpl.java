package com.mattermost.integration.figma.api.figma.file.service;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.UserDataDto;
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
public class FigmaFileServiceImpl implements FigmaFileService {

    private static final String FILES_URL = "https://api.figma.com/v1/projects/%s/files";
    private static final String GET_FILE_URL = "https://api.figma.com/v1/files/%s";


    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserDataKVService userDataKVService;

    @Autowired
    @Qualifier("figmaRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public FigmaProjectFilesDTO getProjectFiles(InputPayload inputPayload) {
        String projectId = inputPayload.getValues().getProject().getValue();
        String refreshToken = inputPayload.getContext().getOauth2().getUser().getRefreshToken();
        String clientId = inputPayload.getContext().getOauth2().getClientId();
        String clientSecret = inputPayload.getContext().getOauth2().getClientSecret();
        String accessToken = oAuthService.refreshToken(clientId, clientSecret, refreshToken).getAccessToken();


        return sendGetProjectFilesRequest(projectId, accessToken);
    }

    @Override
    public FigmaProjectFilesDTO getProjectFiles(String projectId, String figmaUserId, String mmSiteUrl, String botAccessToken) {
        UserDataDto userData = userDataKVService.getUserData(figmaUserId, mmSiteUrl, botAccessToken);
        String accessToken = oAuthService.refreshToken(userData.getClientId(), userData.getClientSecret(), userData.getRefreshToken()).getAccessToken();
        return sendGetProjectFilesRequest(projectId, accessToken);
    }

    private FigmaProjectFilesDTO sendGetProjectFilesRequest(String projectId, String accessToken) {
        String url = String.format(FILES_URL, projectId);

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", accessToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<FigmaProjectFilesDTO> resp = restTemplate.exchange(url, HttpMethod.GET, request, FigmaProjectFilesDTO.class);
        return resp.getBody();
    }


    @Override
    public FigmaProjectFileDTO getFileByKey(String fileKey, String accessToken) {
        String url = String.format(GET_FILE_URL, fileKey);

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", accessToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<FigmaProjectFileDTO> resp = restTemplate.exchange(url, HttpMethod.GET, request, FigmaProjectFileDTO.class);
        return resp.getBody();
    }
}
