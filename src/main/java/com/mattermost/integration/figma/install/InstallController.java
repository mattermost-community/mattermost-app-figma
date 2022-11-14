package com.mattermost.integration.figma.install;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattermost.integration.figma.api.figma.notification.service.FileNotificationService;
import com.mattermost.integration.figma.api.mm.bindings.BindingService;
import com.mattermost.integration.figma.api.mm.kv.KVService;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.input.mm.binding.BindingsDTO;
import com.mattermost.integration.figma.input.mm.manifest.Http;
import com.mattermost.integration.figma.input.mm.manifest.Manifest;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.mattermost.integration.figma.constant.prefixes.webhook.TeamWebhookPrefixes.TEAM_WEBHOOK_PREFIX;

@RestController
@Slf4j
public class InstallController {

    @Value("${app.root.url}")
    private String appRootUrl;

    @Autowired
    private BindingService bindingService;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserDataKVService userDataKVService;

    @Autowired
    private FileNotificationService fileNotificationService;

    @Autowired
    private KVService kvService;

    @GetMapping("/manifest.json")
    public Manifest getManifest() throws IOException {
        log.info("Receive install request ");
        String manifest = jsonUtils.readJsonFile("classpath:static/manifest.json");
        Optional<Manifest> manifestOptional = jsonUtils.convertStringToObject(manifest, Manifest.class);

        Http http = new Http();
        http.setRootUrl(appRootUrl);

        if (manifestOptional.isEmpty()) {
            throw new RuntimeException("Manifest was not found");
        }

        Manifest man = manifestOptional.get();
        man.setWebhookAuthType("none");
        man.setHttp(http);
        return man;
    }

    @PostMapping(value = "/bindings")
    public BindingsDTO postBindings(@RequestBody String payloadString) throws IOException {
        InputPayload payload = mapper.readValue(payloadString, InputPayload.class);
        return bindingService.filterBindingsDependingOnUser(payload);
    }

    @PostMapping("/uninstall")
    public String onUninstall(@RequestBody String payloadString) throws JsonProcessingException {
        log.info("Receive uninstall request: " + payloadString);

        InputPayload payload = mapper.readValue(payloadString, InputPayload.class);

        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String botAccessToken = payload.getContext().getBotAccessToken();

        Set<String> allFigmaTeamIds = userDataKVService.getAllFigmaTeamIds(mattermostSiteUrl, botAccessToken);

        if (Objects.isNull(allFigmaTeamIds)) {
            log.info("Uninstall with empty webhooks: " + payloadString);
            return "{\"type\":\"ok\",\"text\":\"Figma webhooks were deleted\"}";
        }

        for (String teamId : allFigmaTeamIds) {
            String currentTeamWebhookId = kvService.get(TEAM_WEBHOOK_PREFIX.concat(teamId), mattermostSiteUrl, botAccessToken);
            fileNotificationService.deleteSingleFileCommentWebhook(currentTeamWebhookId, teamId, mattermostSiteUrl, botAccessToken);
        }

        return "{\"type\":\"ok\",\"text\":\"Figma webhooks were deleted\"}";
    }

    @PostMapping("/ping")
    public String lambdaPing() {
        return "{\"type\":\"ok\",\"text\":\"PONG\"}";
    }
}
