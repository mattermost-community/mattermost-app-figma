package com.mattermost.integration.figma.api.mm.dm.dto;

import lombok.Data;

@Data
public class DMMessagePayload {
    private String channelId;
    private String message;
    private String token;
    private String mmSiteUrlBase;
}
