package com.mattermost.integration.figma.api.figma.user.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class Child {
    private String id;
    private String name;
    private String type;
    private List<Object> children;
    private BackgroundColor backgroundColor;
    private Object prototypeStartNodeID;
    private List<Object> flowStartingPoints;
    private PrototypeDevice prototypeDevice;
}
