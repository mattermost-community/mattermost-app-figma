package com.mattermost.integration.figma.input.mm.manifest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mattermost.integration.figma.input.mm.binding.Expand;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bindings {
    private String path;
    private Expand expand;
}
