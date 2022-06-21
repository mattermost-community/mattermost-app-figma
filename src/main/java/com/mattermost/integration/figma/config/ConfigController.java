package com.mattermost.integration.figma.config;

import com.mattermost.integration.figma.api.mm.bindings.BindingService;
import com.mattermost.integration.figma.input.mm.binding.BindingsDTO;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class ConfigController {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private BindingService bindingService;

    @PostMapping(value = "/bindings", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BindingsDTO postBindings(@RequestBody InputPayload payload) {
        return bindingService.filterBindingsDependingOnUser(payload);
    }

    @GetMapping(value = "/static/icon.png", produces= MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getIcon() throws IOException {
        final Resource resource = resourceLoader.getResource("classpath:static/icon.png");
        return  IOUtils.toByteArray(resource.getInputStream());

    }

}
