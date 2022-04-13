package com.mattermost.integration.figma.api.mm.dm.dto;

import lombok.Data;

@Data
public class DMMessageWithPropsPayload {
    private String body;
    private String token;
    private String mmSiteUrl;
}
