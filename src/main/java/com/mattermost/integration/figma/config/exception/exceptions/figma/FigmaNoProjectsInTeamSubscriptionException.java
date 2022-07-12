package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaNoProjectsInTeamSubscriptionException extends RuntimeException {

    public FigmaNoProjectsInTeamSubscriptionException() {
        super("This Figma team has no projects you can subscribe to.");
    }
}
