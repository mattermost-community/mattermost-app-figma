package com.mattermost.integration.figma.utils.json;

import com.fasterxml.jackson.core.JsonParser;
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
}
