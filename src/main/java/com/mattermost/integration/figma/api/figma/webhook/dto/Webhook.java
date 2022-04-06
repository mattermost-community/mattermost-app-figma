package com.mattermost.integration.figma.api.figma.webhook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Webhook {
    @JsonProperty("id")
    private String id;
    @JsonProperty("team_id")
    private String teamId;
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("endpoint")
    private String endpoint;
    @JsonProperty("passcode")
    private String passcode;
    @JsonProperty("status")
    private String status;
    @JsonProperty("description")
    private Object description;
    @JsonProperty("protocol_version")
    private String protocolVersion;
}
