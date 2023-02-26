package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaBadRequestException extends RuntimeException {

    public FigmaBadRequestException() {
        super("figma bad request");
    }
}
