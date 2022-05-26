package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMSubscriptionToFileInSubscribedProjectException extends RuntimeException {
    public MMSubscriptionToFileInSubscribedProjectException(String projectName) {
        super(String.format("You are already subscribed to this file via %s project", projectName));
    }
}
