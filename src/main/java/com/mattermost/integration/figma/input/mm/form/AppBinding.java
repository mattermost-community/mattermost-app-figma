package com.mattermost.integration.figma.input.mm.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AppBinding {
    @JsonProperty("app_id")
    private String appId;
    private String label;
    private String description;
    private List<Binding> bindings;
}
