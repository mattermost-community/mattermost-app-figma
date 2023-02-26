package com.mattermost.integration.figma.api.mm.kv.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FileInfo {
    @EqualsAndHashCode.Include
    private String fileId;
    private String fileName;
    private String userId;
    private LocalDate createdAt;
    private String figmaUserId;
}
