package com.mattermost.integration.figma.input.mm.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Field {
    private String name;
    private String type;
    private String value;
    @JsonProperty("is_required")
    private boolean isRequired;
    @JsonProperty("modal_label")
    private String label;
}
