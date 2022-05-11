package com.mattermost.integration.figma.config;

import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Controller
public class ConfigController {

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping(value = "/bindings", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postBindings() throws IOException {
        return jsonUtils.readJsonFile("classpath:static/bindings.json");
    }

    @GetMapping(value = "/static/icon.png", produces= MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getIcon() throws IOException {
        final Resource resource = resourceLoader.getResource("classpath:static/icon.png");
        return  IOUtils.toByteArray(resource.getInputStream());

    }

}
