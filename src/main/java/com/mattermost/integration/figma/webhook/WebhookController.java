package com.mattermost.integration.figma.webhook;


import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.api.figma.notification.service.FileNotificationService;
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

    public WebhookController(FileNotificationService fileNotificationService, FileCommentService fileCommentService) {
        this.fileNotificationService = fileNotificationService;
        this.fileCommentService = fileCommentService;
    }

    @PostMapping("/comment")
    public void comment(@RequestBody FileCommentWebhookResponse response) {
        if ("PING".equals(response.getValues().getData().getEventType())) {
            log.debug(response.toString());
            return;
        }
        log.debug("Received webhook from figma: " + response);
        fileCommentService.updateName(response);
        fileNotificationService.sendFileNotificationMessageToMMSubscribedChannels(response);
        fileNotificationService.sendFileNotificationMessageToMM(response);
    }

    @PostMapping("/ping")
    public ResponseEntity<String> lambdaPing() {
        return ResponseEntity.ok("PONG");
    }
}

