package com.mattermost.integration.figma.input.mm.binding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Datum {
    private String location;
    private List<Bindings> bindings;
}
