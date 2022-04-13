package com.mattermost.integration.figma.input.mm.form;

import lombok.Data;

import java.util.List;

@Data
public class Form {
    private String title;
    private String icon;
    private Submit submit;
    private List<Field> fields;
}
