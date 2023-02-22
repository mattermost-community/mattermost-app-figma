package com.mattermost.integration.figma.api.mm.dm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DirectMessageResponseDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("create_at")
    private long createAt;
    @JsonProperty("update_at")
    private long updateAt;
    @JsonProperty("edit_at")
    private int editAt;
    @JsonProperty("delete_at")
    private int deleteAt;
    @JsonProperty("is_pinned")
    private boolean isPinned;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("root_id")
    private String rootId;
    @JsonProperty("original_id")
    private String originalId;
    @JsonProperty("message")
    private String message;
    @JsonProperty("type")
    private String type;
    @JsonProperty("props")
    private Props props;
    @JsonProperty("hashtags")
    private String hashtags;
    @JsonProperty("pending_post_id")
    private String pendingPostId;
    @JsonProperty("reply_count")
    private int replyCount;
    @JsonProperty("last_reply_at")
    private int lastReplyAt;
    @JsonProperty("metadata")
    private Metadata metadata;
}
