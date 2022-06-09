package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaNoFilesInProjectSubscriptionException extends RuntimeException {
    public FigmaNoFilesInProjectSubscriptionException() {
        super("Project has no files");
    }
}
