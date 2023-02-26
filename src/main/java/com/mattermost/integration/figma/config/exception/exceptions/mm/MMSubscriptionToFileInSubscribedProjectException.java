package com.mattermost.integration.figma.config.exception.exceptions.mm;

public class MMSubscriptionToFileInSubscribedProjectException extends RuntimeException {
    public MMSubscriptionToFileInSubscribedProjectException(String projectName, String text) {
        super(String.format(text, projectName));
    }
}
