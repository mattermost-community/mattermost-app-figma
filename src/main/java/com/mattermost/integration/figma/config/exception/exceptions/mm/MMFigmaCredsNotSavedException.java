package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMFigmaCredsNotSavedException extends RuntimeException {

    public MMFigmaCredsNotSavedException() {
        super("Please use /figma configure command before");
    }
}