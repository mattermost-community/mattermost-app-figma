package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMNoAccessTokenException extends RuntimeException {

    public MMNoAccessTokenException(String message) {
        super(message);
    }
}
