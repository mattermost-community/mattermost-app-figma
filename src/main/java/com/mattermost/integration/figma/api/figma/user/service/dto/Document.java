package com.mattermost.integration.figma.api.figma.user.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class Document {
    private String id;
    private String name;
    private String type;
    private List<Child> children;
}
