package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaResourceNotFoundException extends RuntimeException {
    public FigmaResourceNotFoundException() {
        super("Figma resource not found");
    }
}
