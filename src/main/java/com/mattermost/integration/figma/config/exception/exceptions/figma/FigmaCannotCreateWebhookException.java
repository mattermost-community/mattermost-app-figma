package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaCannotCreateWebhookException extends RuntimeException {
    public FigmaCannotCreateWebhookException(String text) {
        super(text);
    }
}
