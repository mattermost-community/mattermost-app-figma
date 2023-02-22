package com.mattermost.integration.figma.api.figma.user.service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FileOwnerResponseDto {
    private Document document;
    private Components components;
    private ComponentSets componentSets;
    private int schemaVersion;
    private Styles styles;
    private String name;
    private Date lastModified;
    private String thumbnailUrl;
    private String version;
    private String role;
    private String editorType;
    private String linkAccess;
}
