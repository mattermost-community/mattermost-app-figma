package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMSubscriptionFromDMChannelException extends RuntimeException{

    public MMSubscriptionFromDMChannelException(){
        super("The Figma subscribe command not runs in direct channel.");
    }
}
