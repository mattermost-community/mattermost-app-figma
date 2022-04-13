package com.mattermost.integration.figma.input.mm.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DMFormMessageReply {
    @JsonProperty("channel_id")
    private String channelId;
    private Props props;
}
