package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaBasicTeamSubscriptionException extends RuntimeException {

    public FigmaBasicTeamSubscriptionException() {
        super("Upgrade to professional team to enable subscription");
    }
}
