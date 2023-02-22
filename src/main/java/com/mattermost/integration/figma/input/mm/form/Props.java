package com.mattermost.integration.figma.input.mm.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Props {
    @JsonProperty("app_bindings")
    private List<AppBinding> appBindings;
}
