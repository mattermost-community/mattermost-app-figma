package com.mattermost.integration.figma.install;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattermost.integration.figma.api.mm.bindings.BindingService;
import com.mattermost.integration.figma.input.mm.binding.BindingsDTO;
import com.mattermost.integration.figma.input.mm.manifest.Http;
import com.mattermost.integration.figma.input.mm.manifest.Manifest;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
public class InstallController {

    @Value("${app.root.url}")
    private String appRootUrl;

    @Autowired
    private BindingService bindingService;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private ObjectMapper mapper;

    @GetMapping("/manifest.json")
    public Manifest getManifest() throws IOException {


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


    @PostMapping("/ping")
    public String lambdaPing() {
        return "{\"type\":\"ok\",\"text\":\"PONG\"}";
    }
}
