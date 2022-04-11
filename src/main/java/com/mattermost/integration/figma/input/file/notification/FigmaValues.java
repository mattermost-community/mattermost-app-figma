package com.mattermost.integration.figma.input.file.notification;

import lombok.Data;

@Data
public class FigmaValues {
    private FigmaWebhookResponse data;
    private Headers headers;
    private String httpMethod;
    private String rawQuery;
}
