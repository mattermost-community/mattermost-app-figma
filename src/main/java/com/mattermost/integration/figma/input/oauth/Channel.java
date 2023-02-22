package com.mattermost.integration.figma.input.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Channel {
    @JsonProperty("create_at")
    private long createAt;
    @JsonProperty("update_at")
    private long updateAt;
    @JsonProperty("team_id")
    private String teamId;
    private String id;
    private String type;
    @JsonProperty("delete_at")
    private int delete_at;
    @JsonProperty("display_name")
    private String displayName;
    private String name;
    private String header;
    private String purpose;
    @JsonProperty("last_post_at")
    private long lastPostAt;
    @JsonProperty("total_msg_count")
    private int totalMsgCount;
    @JsonProperty("extra_update_at")
    private int extraUpdateAt;
    @JsonProperty("creator_id")
    private String creatorId;
    @JsonProperty("scheme_id")
    private Object schemeId;
    private Object props;
    @JsonProperty("group_constrained")
    private Object groupConstrained;
    private Object shared;
    @JsonProperty("total_msg_count_root")
    private int totalMsgCountRoot;
    @JsonProperty("policy_id")
    private Object policyId;
    @JsonProperty("last_root_post_at")
    private long lastRootPostAt;
}
