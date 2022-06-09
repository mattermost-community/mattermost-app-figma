package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaNoProjectsInTeamSubscriptionException extends RuntimeException {

    public FigmaNoProjectsInTeamSubscriptionException() {
        super("Team has no projects");
    }
}
