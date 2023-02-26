package com.mattermost.integration.figma.input.mm.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MMChannelUser {
    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("user_id")
    private String userId;
    private String roles;
    @JsonProperty("last_viewed_at")
    private long lastViewedAt;
    @JsonProperty("msg_count")
    private int msgCount;
    @JsonProperty("mention_count")
    private int mentionCount;
    @JsonProperty("mention_count_root")
    private int mentionCountRoot;
    @JsonProperty("msg_count_root")
    private int msgCountRoot;
    @JsonProperty("notify_props")
    private NotifyProps notifyProps;
    @JsonProperty("last_update_at")
    private Object lastUpdateAt;
    @JsonProperty("scheme_guest")
    private boolean schemeGuest;
    @JsonProperty("scheme_user")
    private boolean schemeUser;
    @JsonProperty("scheme_admin")
    private boolean schemeAdmin;
    @JsonProperty("explicit_roles")
    private String explicitRoles;

    @Data
    private class NotifyProps{
        private String desktop;
        private String email;
        @JsonProperty("ignore_channel_mention")
        private String ignoreChannelMentions;
        @JsonProperty("mark_unread")
        private String markUnread;
        private String push;
    }
}
