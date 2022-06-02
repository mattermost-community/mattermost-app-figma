package com.mattermost.integration.figma.api.mm.kv.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectInfo {
    private String projectId;
    private String name;
    private String userId;
    private LocalDate createdAt;
    private String figmaUserId;
}
