package com.mattermost.integration.figma.api.mm.dm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectMessageDTO {
    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("message")
    private String message;
}
