package com.mattermost.integration.figma.subscribe.service.dto;

import lombok.Data;

@Data
public class FileData {
    private String fileKey;
    private String fileName;
    private String subscribedBy;
    private String figmaUserId;
}
