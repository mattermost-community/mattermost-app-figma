package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMForbiddenException extends RuntimeException {

    public MMForbiddenException(String message) {
        super(message);
    }
}
