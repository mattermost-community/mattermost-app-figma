package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMFigmaUserNotSavedException extends RuntimeException {

    public MMFigmaUserNotSavedException() {
        super("Configure the Figma integration first using the /figma slash command.");
    }
}
