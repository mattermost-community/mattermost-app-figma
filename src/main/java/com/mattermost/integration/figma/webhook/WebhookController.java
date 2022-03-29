package com.mattermost.integration.figma.webhook;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {


    @PostMapping("/comment")
    public void comment(@RequestBody String request) {
        System.out.println("Got it "+ request);
    }
}
