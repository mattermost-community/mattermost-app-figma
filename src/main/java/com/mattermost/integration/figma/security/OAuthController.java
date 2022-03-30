package com.mattermost.integration.figma.security;

import com.mattermost.integration.figma.input.InputPayload;
import com.mattermost.integration.figma.security.dto.FigmaTokenDTO;
import com.mattermost.integration.figma.security.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @PostMapping("/oauth2/connect")
    public String getOauthForm(@RequestBody String params) {
        System.out.println(params);
        String url = oAuthService.generateUrl(params);
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
        System.out.println(payload.getContext().getActingUserAccessToken());
        oAuthService.storeOAuthCreds(payload);
        return "{\"text\":\"saved\"}";
    }

    @PostMapping("/connect")
    public String connect(@RequestBody String params) {
        System.out.println(params);
        String url = oAuthService.getConnectUrl(params);

        return String.format("{\"type\":\"ok\",\"text\":\"[Connect](%s) to Figma.\"}", url);
    }


}
