package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMFieldLoadException extends RuntimeException {
    public MMFieldLoadException(String fieldName) {
        super("Please wait for " + fieldName + " field to load");
    }
}
