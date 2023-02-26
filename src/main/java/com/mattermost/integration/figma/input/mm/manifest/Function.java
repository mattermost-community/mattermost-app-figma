package com.mattermost.integration.figma.input.mm.manifest;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Function {
    public String path;
    public String name;
    public String handler;
    public String runtime;
}
