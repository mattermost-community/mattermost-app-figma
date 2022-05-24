package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMFileInfoNotFoundException extends RuntimeException {
    public MMFileInfoNotFoundException() {
        super("File not found in KV");
    }

    public MMFileInfoNotFoundException(String fileKey) {
        super(String.format("File with key %s not found in KV", fileKey));
    }
}
