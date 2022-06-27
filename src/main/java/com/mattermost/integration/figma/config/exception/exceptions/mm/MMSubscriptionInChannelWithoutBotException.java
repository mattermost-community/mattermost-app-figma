package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMSubscriptionInChannelWithoutBotException extends RuntimeException {
    public MMSubscriptionInChannelWithoutBotException(String message) {
        super(message);
    }

}
