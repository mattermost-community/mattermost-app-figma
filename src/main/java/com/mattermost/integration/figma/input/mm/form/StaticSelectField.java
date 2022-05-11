package com.mattermost.integration.figma.input.mm.form;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class StaticSelectField extends Field {

    private List<Option> options;

}
