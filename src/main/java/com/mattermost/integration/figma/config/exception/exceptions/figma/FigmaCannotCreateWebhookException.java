package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaCannotCreateWebhookException extends RuntimeException {
    public FigmaCannotCreateWebhookException() {
        super("Cannot create subscription for current team. Please contact team admin to create primary subscription.");
    }
}
