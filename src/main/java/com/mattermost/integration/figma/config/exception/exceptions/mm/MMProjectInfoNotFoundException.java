package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMProjectInfoNotFoundException extends RuntimeException {
    public MMProjectInfoNotFoundException(String projectKey) {
        super(String.format("Project with key %s not found in KV", projectKey));
    }
}
