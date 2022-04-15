package com.mattermost.integration.figma.input.mm.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TextField extends Field {

    @JsonProperty("subtype")
    private String subType;
}
