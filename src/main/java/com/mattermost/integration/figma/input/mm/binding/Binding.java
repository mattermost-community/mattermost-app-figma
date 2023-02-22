package com.mattermost.integration.figma.input.mm.binding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Binding {
    @JsonProperty("app_id")
    private String appId;
    private String location;
    private String label;
    private Submit submit;
    private Form form;
}
