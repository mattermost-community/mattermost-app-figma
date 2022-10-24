package com.mattermost.integration.figma.webhook;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.api.figma.notification.service.FileNotificationService;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.webhook.service.FileCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class WebhookController {

    private final FileNotificationService fileNotificationService;
    private final FileCommentService fileCommentService;
    private final ObjectMapper mapper;

    public WebhookController(FileNotificationService fileNotificationService, FileCommentService fileCommentService, ObjectMapper mapper) {
        this.fileNotificationService = fileNotificationService;
        this.fileCommentService = fileCommentService;
        this.mapper = mapper;
    }

    @PostMapping("/comment")
    public void comment(@RequestBody String responseString) throws JsonProcessingException {


        log.debug(responseString);

        FileCommentWebhookResponse response = mapper.readValue(responseString, FileCommentWebhookResponse.class);

        if ("PING".equals(response.getValues().getData().getEventType())) {
            log.debug(response.toString());
            return;
        }
        log.info("Received webhook from figma: " + response);
        fileCommentService.updateName(response);
        fileNotificationService.sendFileNotificationMessageToMMSubscribedChannels(response);
        fileNotificationService.sendFileNotificationMessageToMM(response);
    }
}

