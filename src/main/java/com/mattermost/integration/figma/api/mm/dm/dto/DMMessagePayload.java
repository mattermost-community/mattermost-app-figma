package com.mattermost.integration.figma.api.mm.dm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DMMessagePayload {
    private String channelId;
    private String message;
    private String token;
    private String mmSiteUrlBase;
}
