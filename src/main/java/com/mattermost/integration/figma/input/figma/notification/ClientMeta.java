package com.mattermost.integration.figma.input.figma.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClientMeta {
    @JsonProperty("node_id")
    private String nodeId;
    @JsonProperty("node_offset")
    private NodeOffset nodeOffset;
}
