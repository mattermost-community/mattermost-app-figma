package com.mattermost.integration.figma.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

@Component
@Slf4j
public class JsonUtils {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    public Optional convertStringToObject(String jsonString, Class type) {
        try {
           return Optional.of(objectMapper.readValue(jsonString, type));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public String readJsonFile(String path) throws IOException {
        final Resource resource = resourceLoader.getResource(path);
        Reader reader = new InputStreamReader(resource.getInputStream());
        return   FileCopyUtils.copyToString(reader).replaceAll("\n","");
    }

    public Optional convertStringToObject(String jsonString, TypeReference type) {
        try {
            return Optional.of(objectMapper.readValue(jsonString, type));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }
}
