package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMBadRequestException extends RuntimeException {

    public MMBadRequestException(String message) {
        super(message);
    }
}
