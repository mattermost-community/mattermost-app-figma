package com.mattermost.integration.figma.input.mm.binding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submit {
    private String path;
    private Expand expand;
}
