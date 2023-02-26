package com.mattermost.integration.figma.api.figma.webhook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamWebhookInfoResponseDto {
    @JsonProperty("webhooks")
    public List<Webhook> webhooks;
}
