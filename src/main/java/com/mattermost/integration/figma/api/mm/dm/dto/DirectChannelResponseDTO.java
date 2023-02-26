package com.mattermost.integration.figma.api.mm.dm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DirectChannelResponseDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("create_at")
    private long createAt;
    @JsonProperty("update_at")
    private long updateAt;
    @JsonProperty("delete_at")
    private int deleteAt;
    @JsonProperty("team_id")
    private String teamId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("name")
    private String name;
    @JsonProperty("header")
    private String header;
    @JsonProperty("purpose")
    private String purpose;
    @JsonProperty("last_post_at")
    private long lastPostAt;
    @JsonProperty("total_msg_count")
    private int totalMsgCount;
    @JsonProperty("extra_update_at")
    private int extraUpdateAt;
    @JsonProperty("creatorId")
    private String creator_id;
    @JsonProperty("shared")
    private boolean shared;
    @JsonProperty("total_msg_count_root")
    private int totalMsgCountRoot;
    @JsonProperty("last_root_post_at")
    private long lastRootPostAt;
}
