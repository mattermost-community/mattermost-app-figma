package com.mattermost.integration.figma.install;

import com.mattermost.integration.figma.input.mm.manifest.Http;
import com.mattermost.integration.figma.input.mm.manifest.Manifest;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
public class InstallController {

    @Value("${app.root.url}")
    private String appRootUrl;

    @Autowired
    private JsonUtils jsonUtils;

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


    @PostMapping("/ping")
    public String lambdaPing() {
        return "{\"type\":\"ok\",\"text\":\"PONG\"}";
    }
}
