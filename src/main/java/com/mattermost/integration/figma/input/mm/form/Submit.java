package com.mattermost.integration.figma.input.mm.form;

import com.mattermost.integration.figma.input.mm.binding.Expand;
import lombok.Data;

import java.util.Map;

@Data
public class Submit {
    private String path;
    private Expand expand;
    private Map<String, String > state;
}
