package com.mattermost.integration.figma.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class JsonUtils {

    @Autowired
    private ObjectMapper objectMapper;

    public Optional convertStringToObject(String jsonString, Class type) {
        try {
           return Optional.of(objectMapper.readValue(jsonString, type));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
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
