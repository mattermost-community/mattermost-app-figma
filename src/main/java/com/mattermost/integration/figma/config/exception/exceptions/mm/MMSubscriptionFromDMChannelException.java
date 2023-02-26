package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMSubscriptionFromDMChannelException extends RuntimeException {

    public MMSubscriptionFromDMChannelException() {
        super("Can’t subscribe a channel bot to Figma notifications.");
    }

    public MMSubscriptionFromDMChannelException(String message) {
        super(message);

    }
}
