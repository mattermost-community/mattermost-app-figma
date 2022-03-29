package com.mattermost.integration.figma.security;

import com.mattermost.integration.figma.input.InputPayload;
import com.mattermost.integration.figma.security.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping("/oauth/callback")
    public void getCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        System.out.println(code + "////" + state);
    }

    @PostMapping("/oauth2/connect")
    public String getOauthForm(@RequestBody String params) {
        System.out.println(params);
        String url = oAuthService.generateUrl(params);
        return String.format("{\"type\":\"ok\",\"data\":\"%s\"}", url);
    }

    @PostMapping("/oauth2/complete")
    public String postOauthClientSecret(@RequestBody String params) {
        System.out.println(params);
        return "\"text\":\"yo\"";
    }

    @PostMapping("/configure")
    public String posOauthCreds(@RequestBody InputPayload payload) {
        System.out.println(payload.getContext().getActingUserAccessToken());
        oAuthService.storeOAuthCreds(payload);
        return "{\"text\":\"yo\"}";
    }

    @PostMapping("/connect")
    public String connect(@RequestBody String params) {
        System.out.println(params);
        String url = oAuthService.getConnectUrl(params);

        return String.format("{\"type\":\"ok\",\"text\":\"[Connect](%s) to Figma.\"}", url);
    }


}
