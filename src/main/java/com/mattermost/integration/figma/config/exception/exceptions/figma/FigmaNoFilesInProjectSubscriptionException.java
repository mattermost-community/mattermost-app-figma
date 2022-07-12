package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaNoFilesInProjectSubscriptionException extends RuntimeException {
    public FigmaNoFilesInProjectSubscriptionException() {
        super("This Figma project has no files to subscribe to.");
    }
}
