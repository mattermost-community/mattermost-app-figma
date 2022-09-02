package com.mattermost.integration.figma.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMFigmaCredsNotSavedException;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.input.oauth.OAuth2;
import com.mattermost.integration.figma.security.dto.FigmaTokenDTO;
import com.mattermost.integration.figma.security.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserDataKVService userDataKVService;

    @Autowired
    private ObjectMapper mapper;

    @PostMapping("/oauth2/connect")
    public String getOauthForm(@RequestBody String payloadString) throws JsonProcessingException {
        log.debug(payloadString);

        InputPayload payload = mapper.readValue(payloadString, InputPayload.class);

        if (!hasFigmaCredsStored(payload)) {
            log.error("Figma credentials were not stored:" + payload);
            throw new MMFigmaCredsNotSavedException();
        }

        String url = oAuthService.generateUrl(payload);
        return String.format("{\"type\":\"ok\",\"data\":\"%s\"}", url);
    }

    @PostMapping("/oauth2/complete")
    public String postOauthClientSecret(@RequestBody String payloadString) throws JsonProcessingException {
        log.debug(payloadString);

        InputPayload payload = mapper.readValue(payloadString, InputPayload.class);

        FigmaTokenDTO figmaUserToken = oAuthService.getFigmaUserToken(payload);

        oAuthService.storeFigmaUserToken(payload, figmaUserToken);
        oAuthService.storeUserDataIntoKV(payload, figmaUserToken);
        userDataKVService.changeUserConnectionStatus(payload.getContext().getActingUser().getId(), true,
                payload.getContext().getMattermostSiteUrl(), payload.getContext().getBotAccessToken());

        return "{\"type\":\"ok\",\"text\":\"You have successfully connected your Figma account. Type in /figma to get started .\"}";
    }

    @PostMapping("/configure")
    public String posOauthCreds(@RequestBody String payloadString) throws JsonProcessingException {
        log.debug(payloadString);
        InputPayload payload = mapper.readValue(payloadString, InputPayload.class);

        oAuthService.storeOAuthCreds(payload);
        return "{\"text\":\"saved\"}";
    }

    @PostMapping("/connect")
    public String connect(@RequestBody String payloadString) throws JsonProcessingException {
        log.debug("Connect request payload: " + payloadString);

        InputPayload payload = mapper.readValue(payloadString, InputPayload.class);

        String url = oAuthService.getConnectUrl(payload);

        return String.format("{\"type\":\"ok\",\"text\":\"[Connect](%s) to Figma.\"}", url);
    }

    @PostMapping("/disconnect")
    public String disconnect(@RequestBody String payloadString) throws JsonProcessingException {
        log.debug("Disconnect request payload: " + payloadString);

        InputPayload payload = mapper.readValue(payloadString, InputPayload.class);


        FigmaTokenDTO figmaTokenDTO = new FigmaTokenDTO();
        oAuthService.storeFigmaUserToken(payload, figmaTokenDTO);
        userDataKVService.changeUserConnectionStatus(payload.getContext().getActingUser().getId(), false,
                payload.getContext().getMattermostSiteUrl(), payload.getContext().getBotAccessToken());

        return "{\"type\":\"ok\",\"text\":\"You have successfully disconnected your account from Figma\"}";
    }

    private boolean hasFigmaCredsStored(InputPayload payload) {
        OAuth2 oauth2 = payload.getContext().getOauth2();
        if (Objects.isNull(oauth2)) {
            return false;
        }

        if (StringUtils.isBlank(oauth2.getClientId())) {
            return false;
        }

        if (StringUtils.isBlank(oauth2.getCompleteUrl())) {
            return false;
        }
        return true;
    }

}
