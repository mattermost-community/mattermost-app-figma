package com.mattermost.integration.figma.security;

import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.FigmaTokenDTO;
import com.mattermost.integration.figma.security.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @PostMapping("/oauth2/connect")
    public String getOauthForm(@RequestBody InputPayload payload) {
        System.out.println(payload);
        String url = oAuthService.generateUrl(payload);
        return String.format("{\"type\":\"ok\",\"data\":\"%s\"}", url);
    }

    @PostMapping("/oauth2/complete")
    public String postOauthClientSecret(@RequestBody InputPayload payload) {

        FigmaTokenDTO figmaUserToken = oAuthService.getFigmaUserToken(payload);
        oAuthService.storeFigmaUserToken(payload, figmaUserToken);

        return "{\"text\":\"completed\"}";
    }

    @PostMapping("/configure")
    public String posOauthCreds(@RequestBody InputPayload payload) {
        oAuthService.storeOAuthCreds(payload);
        return "{\"text\":\"saved\"}";
    }

    @PostMapping("/connect")
    public String connect(@RequestBody InputPayload payload) {
        log.debug("Connect request payload: " + payload);
        String url = oAuthService.getConnectUrl(payload);

        return String.format("{\"type\":\"ok\",\"text\":\"[Connect](%s) to Figma.\"}", url);
    }


}
