package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMSubscriptionInChannelWithoutBotException extends RuntimeException {
    public MMSubscriptionInChannelWithoutBotException() {
        super("Please add Figma bot to this channel");
    }

}
