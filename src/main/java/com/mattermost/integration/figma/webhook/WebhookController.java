package com.mattermost.integration.figma.webhook;


import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.api.figma.notification.service.FileNotificationService;
import lombok.extern.slf4j.Slf4j;
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

    public WebhookController(FileNotificationService fileNotificationService) {
        this.fileNotificationService = fileNotificationService;
    }

    @PostMapping("/comment")
    public void comment(@RequestBody FileCommentWebhookResponse response) {
        System.out.println(response);
        if (Objects.nonNull(response) && !response.getValues().getData().getEventType().equals("PING")) {
            log.debug("Received webhook from figma: " + response);
            fileNotificationService.sendFileNotificationMessageToMMSubscribedChannels(response);
            fileNotificationService.sendFileNotificationMessageToMM(response);
        }
    }
}
