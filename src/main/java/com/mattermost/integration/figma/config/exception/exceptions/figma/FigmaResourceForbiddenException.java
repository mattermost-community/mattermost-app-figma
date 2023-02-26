package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaResourceForbiddenException extends RuntimeException {

    public FigmaResourceForbiddenException() {
        super("Figma resource forbidden");
    }
}
