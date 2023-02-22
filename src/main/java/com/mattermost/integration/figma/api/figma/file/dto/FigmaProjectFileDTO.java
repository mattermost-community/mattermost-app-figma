package com.mattermost.integration.figma.api.figma.file.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FigmaProjectFileDTO {
    private String key;
    private String name;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    @JsonProperty("last_modified")
    private Date lastModified;
}
