package com.mattermost.integration.figma.input.file.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class FileCommentWebhookResponse {
    @JsonProperty("comment")
    private Comment[] comment;
    @JsonProperty("comment_id")
    private String commentId;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("file_key")
    private String fileKey;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("mentions")
    private String[] mentions;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("passcode")
    private String passcode;
    @JsonProperty("protocolVersion")
    private String protocol_version;
    @JsonProperty("resolved_at")
    private String resolvedAt;
    @JsonProperty("retries")
    private String retries;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("triggered_by")
    private User triggeredBy;
    @JsonProperty("webhook_id")
    private String webhookId;
}
