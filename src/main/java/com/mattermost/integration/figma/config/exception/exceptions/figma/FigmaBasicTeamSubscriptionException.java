package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaBasicTeamSubscriptionException extends RuntimeException {

    public FigmaBasicTeamSubscriptionException() {
        super("Upgrade to Figma Professional to enable team subscriptions.");
    }
}
