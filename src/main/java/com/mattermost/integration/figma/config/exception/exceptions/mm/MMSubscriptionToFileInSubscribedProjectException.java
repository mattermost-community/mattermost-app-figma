package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMSubscriptionToFileInSubscribedProjectException extends RuntimeException {
    public MMSubscriptionToFileInSubscribedProjectException(String projectName) {
        super(String.format("You’re already subscribed to this file through the %s project.", projectName));
    }
}
