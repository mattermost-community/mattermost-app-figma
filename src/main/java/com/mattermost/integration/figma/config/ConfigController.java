package com.mattermost.integration.figma.config;

import jdk.jfr.ContentType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Controller
public class ConfigController {

    @Autowired
    ResourceLoader resourceLoader;

    @PostMapping(value = "/bindings", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postBindings() throws IOException {
        try {

            final Resource resource = resourceLoader.getResource("classpath:static/bindings.json");
            Reader reader = new InputStreamReader(resource.getInputStream());
            String filedata =  FileCopyUtils.copyToString(reader);
            return filedata;
        } catch (Exception e) {
            e.printStackTrace();
            return "oops";
        }
    }

    @GetMapping(value = "/static/icon.png", produces= MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getIcon() throws IOException {
        final Resource resource = resourceLoader.getResource("classpath:static/icon.png");
        return  IOUtils.toByteArray(resource.getInputStream());

    }

}
