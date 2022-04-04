package com.mattermost.integration.figma.notification.service;

import com.mattermost.integration.figma.input.oauth.InputPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
public class FileNotificationController {
    private final FileNotificationService fileNotificationService;

    public FileNotificationController(FileNotificationService fileNotificationService) {
        this.fileNotificationService = fileNotificationService;
    }

    @PostMapping("/subscribe")
    public String subscribeToFileComment(@RequestBody InputPayload request) {
        log.info("Subscription to file comment from user with id: " + request.getContext().getUserAgent() + " has come");
        log.debug("Subscription to file comment request: " + request);
        if (Objects.nonNull(fileNotificationService.subscribeToFileNotification(request.getValues().getTeamId(),
                request.getContext().getApp().getWebhookSecret()))) {
            return "{\"text\" : \"Success\"}";
        }
        return "{\"text\" : \"There is no such team id\"}";
    }
}
