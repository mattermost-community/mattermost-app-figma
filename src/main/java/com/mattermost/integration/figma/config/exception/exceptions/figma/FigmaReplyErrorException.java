package com.mattermost.integration.figma.config.exception.exceptions.figma;

public class FigmaReplyErrorException extends RuntimeException {
    public FigmaReplyErrorException(String error) {

        super(error);
    }

}
