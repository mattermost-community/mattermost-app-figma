package com.mattermost.integration.figma.config;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LifecycleController {

    @PostMapping("/install")
    public void onInstall(@RequestBody String params) {
        System.out.println(params);
    }
}
