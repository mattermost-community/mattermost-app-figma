package com.mattermost.integration.figma.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class JsonUtils {

    @Autowired
    private ObjectMapper objectMapper;

    public  Optional<String> getJsonValue(String params, String key) {
        try {
            JsonNode node = objectMapper.readTree(params) ;
            return Optional.of(node.findValue(key).asText());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional convertStringToObject(String jsonString, Class type) {
        try {
           return Optional.of(objectMapper.readValue(jsonString, type));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
