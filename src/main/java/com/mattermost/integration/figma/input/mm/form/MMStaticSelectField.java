package com.mattermost.integration.figma.input.mm.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MMStaticSelectField {
    private String label;
    private String value;
}
