package com.mattermost.integration.figma.input.mm.binding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bindings {
    @JsonProperty("app_id")
    private String appId;
    private String location;
    private String icon;
    private String label;
    private String description;
    private List<Binding> bindings;
}
