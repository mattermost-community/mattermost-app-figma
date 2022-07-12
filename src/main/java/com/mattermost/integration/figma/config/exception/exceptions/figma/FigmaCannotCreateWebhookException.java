package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaCannotCreateWebhookException extends RuntimeException {
    public FigmaCannotCreateWebhookException() {
        super("Can’t subscribe to this Figma team. Contact the team admin to set up a primary subscription.");
    }
}
