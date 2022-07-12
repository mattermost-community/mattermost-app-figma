package com.mattermost.integration.figma.input.mm.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public class Form {
    private String title;
    private Submit submit;
    private List<Field> fields;
    private Submit source;
}
