package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMFigmaUserNotSavedException extends RuntimeException {

    public MMFigmaUserNotSavedException() {
        super("Please use /figma connect command before");
    }
}
