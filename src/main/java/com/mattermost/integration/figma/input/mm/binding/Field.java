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
public class Field {
    private String name;
    private String type;
    @JsonProperty("is_required")
    private Boolean isRequired;
    private String label;
}
