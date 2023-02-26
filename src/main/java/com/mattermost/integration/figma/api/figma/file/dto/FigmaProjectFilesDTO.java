package com.mattermost.integration.figma.api.figma.file.dto;

import lombok.Data;

import java.util.List;

@Data
public class FigmaProjectFilesDTO {
    private String name;
    private List<FigmaProjectFileDTO> files;
}
