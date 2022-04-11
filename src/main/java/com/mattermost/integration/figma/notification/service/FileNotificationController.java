package com.mattermost.integration.figma.notification.service;

import com.mattermost.integration.figma.api.mm.kv.KVService;
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
    private final KVService kvService;

    public FileNotificationController(FileNotificationService fileNotificationService, KVService kvService) {
        this.fileNotificationService = fileNotificationService;
        this.kvService = kvService;
    }

    @PostMapping("/subscribe")
    public String subscribeToFileComment(@RequestBody InputPayload request) {
        System.out.println(request);
        log.info("Subscription to file comment from user with id: " + request.getContext().getUserAgent() + " has come");
        log.debug("Subscription to file comment request: " + request);

        if (SubscribeToFileNotification.SUBSCRIBED.equals(fileNotificationService.subscribeToFileNotification(request))) {
            fileNotificationService.saveUserData(request);
            return "{\"text\" : \"Success\"}";
        }
        return "{\"text\" : \"You are successfully subscribed to an existing webhook\"}";
    }
}
