package com.mattermost.integration.figma.api.figma.notification.controller;


import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.api.figma.notification.service.FileNotificationService;
import com.mattermost.integration.figma.api.figma.notification.service.SubscribeToFileNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SubscribeController {
    private final FileNotificationService fileNotificationService;
    private final UserDataKVService userDataKVService;

    public SubscribeController(FileNotificationService fileNotificationService, UserDataKVService userDataKVService) {
        this.fileNotificationService = fileNotificationService;
        this.userDataKVService = userDataKVService;
    }

    @PostMapping("/subscribe")
    public String subscribeToFileComment(@RequestBody InputPayload request) {
        System.out.println(request);
        log.info("Subscription to file comment from user with id: " + request.getContext().getUserAgent() + " has come");
        log.debug("Subscription to file comment request: " + request);

        //TODO rewrite logic for updating updating webhook and k/v data
        if (SubscribeToFileNotification.SUBSCRIBED.equals(fileNotificationService.subscribeToFileNotification(request))) {
            userDataKVService.saveUserData(request);
            return "{\"text\" : \"Success\"}";
        }
        userDataKVService.saveUserData(request);
        return "{\"text\" : \"You are successfully subscribed to an existing webhook\"}";
    }
}
