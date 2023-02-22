package com.mattermost.integration.figma.input.mm.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Option {

    private String label;
    private String value;
}
