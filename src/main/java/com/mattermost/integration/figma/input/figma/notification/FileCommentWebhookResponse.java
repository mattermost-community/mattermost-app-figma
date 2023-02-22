package com.mattermost.integration.figma.input.figma.notification;

import com.mattermost.integration.figma.input.oauth.Context;
import lombok.Data;

@Data
public class FileCommentWebhookResponse {
    private String path;
    private FigmaValues values;
    private Context context;
}
