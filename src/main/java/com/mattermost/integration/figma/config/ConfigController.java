package com.mattermost.integration.figma.config;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class ConfigController {

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping(value = "/static/icon.png", produces= MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getIcon() throws IOException {
        final Resource resource = resourceLoader.getResource("classpath:static/icon.png");
        return  IOUtils.toByteArray(resource.getInputStream());

    }

}
