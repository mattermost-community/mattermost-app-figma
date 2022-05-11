package com.mattermost.integration.figma.input.mm.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class TextField extends Field {

    @JsonProperty("subtype")
    private String subType;
}
