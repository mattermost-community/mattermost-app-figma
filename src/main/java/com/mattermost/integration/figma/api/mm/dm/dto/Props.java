package com.mattermost.integration.figma.api.mm.dm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Props {
    @JsonProperty("from_bot")
    private String fromBot;
}

