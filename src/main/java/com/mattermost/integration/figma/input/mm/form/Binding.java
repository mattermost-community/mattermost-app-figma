package com.mattermost.integration.figma.input.mm.form;

import lombok.Data;

@Data
public class Binding {
    private String location;
    private String label;
    private Form form;
}
