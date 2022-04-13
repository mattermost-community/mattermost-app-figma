package com.mattermost.integration.figma.input.mm.form;

import lombok.Data;

@Data
public class Submit {
    private String path;
    private Expand expand;
}
