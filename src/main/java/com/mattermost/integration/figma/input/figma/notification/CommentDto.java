package com.mattermost.integration.figma.input.figma.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Data
public class CommentDto {
    private String id;
    @JsonProperty("file_key")
    private String fileKey;
    @JsonProperty("parent_id")
    private String parentId;
    private User user;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("resolved_at")
    private Object resolvedAt;
    private String message;
    @JsonProperty("client_meta")
    private ClientMeta clientMeta;
    @JsonProperty("order_id")
    private String orderId;
}
