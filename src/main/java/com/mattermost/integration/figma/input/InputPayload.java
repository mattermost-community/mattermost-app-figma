package com.mattermost.integration.figma.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InputPayload {
    @JsonProperty("path")
    private String path;
    @JsonProperty("expand")
    private Expand expand;
    @JsonProperty("context")
    private Context context;
    @JsonProperty("raw_command")
    private String rawCommand;
    @JsonProperty("values")
    private Values values;
}
