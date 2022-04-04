package com.mattermost.integration.figma.notification.service;

import com.mattermost.integration.figma.input.file.notification.FileCommentNotificationRequest;
import com.mattermost.integration.figma.provider.FigmaTokenProvider;
import com.mattermost.integration.figma.provider.NgrokLinkProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FileNotificationService {
    private static final String BASE_WEBHOOK_URL = "https://api.figma.com/v2/webhooks";
    private static final String PASSCODE = "Mattermost";
    private static final String FILE_COMMENT_EVENT_TYPE = "FILE_COMMENT";
    //Production Mattermost link
    private static final String REDIRECT_URL = "http://localhost:8066/plugins/com.mattermost.apps/apps/spring-boot-example%s?secret=%s";
    private static final String FILE_COMMENT_URL = "/webhook/comment";

    private final RestTemplate restTemplate;

    public FileNotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String subscribeToFileNotification(String teamId, String webhookSecret) {
        if (teamId != null && !teamId.isEmpty() && !teamId.isBlank()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", String.format("Bearer %s", FigmaTokenProvider.token.getAccessToken()));

            FileCommentNotificationRequest fileCommentNotificationRequest = new FileCommentNotificationRequest();
            fileCommentNotificationRequest.setEventType(FILE_COMMENT_EVENT_TYPE);
            fileCommentNotificationRequest.setTeamId(teamId);
            fileCommentNotificationRequest.setPasscode(PASSCODE);
            //For production Mattermost link
            //fileCommentNotificationRequest.setEndpoint(String.format(REDIRECT_URL, FILE_COMMENT_URL, webhookSecret));
            fileCommentNotificationRequest.setEndpoint(NgrokLinkProvider.REDIRECT_URL.concat(FILE_COMMENT_URL));
            log.debug("File notification request : " + fileCommentNotificationRequest);

            HttpEntity<FileCommentNotificationRequest> request = new HttpEntity<>(fileCommentNotificationRequest, headers);
            log.info("Sending comment request for team with id: " + teamId);
            return restTemplate.postForEntity(BASE_WEBHOOK_URL, request, String.class).toString();
        }
        return null;
    }

    public void deleteWebhook(String webhookId) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", String.format("Bearer %s", FigmaTokenProvider.token.getAccessToken()));
        HttpEntity<Object> request = new HttpEntity<>(headers);
        String url = BASE_WEBHOOK_URL.concat("/").concat(webhookId);
        restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        log.info("Successfully deleted webhook with id: " + webhookId);
    }
}
