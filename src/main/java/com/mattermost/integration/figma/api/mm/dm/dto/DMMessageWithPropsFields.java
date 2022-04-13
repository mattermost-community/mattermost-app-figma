package com.mattermost.integration.figma.api.mm.dm.dto;

import lombok.Data;

@Data
public class DMMessageWithPropsFields {
    private String channelId;
    private String appId;
    private String label;
    private String description;
    private String replyCommentId;
    private String replyFileId;
}
