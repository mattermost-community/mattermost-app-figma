package com.mattermost.integration.figma.api.figma.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamProjectDTO {
    private String name;
    private List<ProjectDTO> projects;
}
