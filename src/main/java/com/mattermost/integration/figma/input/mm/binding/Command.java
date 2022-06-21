package com.mattermost.integration.figma.input.mm.binding;

public enum Command {
    CONNECT("connect"),
    DISCONNECT("disconnect"),
    CONFIGURE("configure"),
    SUBSCRIBE("subscribe"),
    LIST("list");

    private String title;

    Command(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
