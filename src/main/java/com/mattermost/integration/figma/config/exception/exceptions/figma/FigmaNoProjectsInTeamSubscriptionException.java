package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaNoProjectsInTeamSubscriptionException extends RuntimeException {

    public FigmaNoProjectsInTeamSubscriptionException(String message) {
        super(message);
    }
}
