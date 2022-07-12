package com.mattermost.integration.figma.input.mm.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Value {
    private String value;
    private String label;
}
