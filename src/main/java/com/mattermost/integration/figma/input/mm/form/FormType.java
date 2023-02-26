package com.mattermost.integration.figma.input.mm.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FormType {
    private Form form;
    private String type;
}
