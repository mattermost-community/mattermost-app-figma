package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.input.mm.binding.Expand;

public class ExpandCreator {

    private static final String ALL = "all";

    public static Expand prepareExpand() {
        Expand expand = new Expand();
        expand.setActingUserAccessToken(ALL);
        expand.setApp(ALL);
        expand.setActingUser(ALL);
        expand.setOauth2App(ALL);
        expand.setOauth2User(ALL);
        expand.setChannel(ALL);
        return expand;
    }
}
