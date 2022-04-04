package com.mattermost.integration.figma.input.file.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class FileCommentNotificationRequest {
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("team_id")
    private String teamId;
    @JsonProperty("endpoint")
    private String endpoint;
    @JsonProperty("passcode")
    private String passcode;
}
