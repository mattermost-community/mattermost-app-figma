package com.mattermost.integration.figma.api.mm.dm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DMChannelPayload {
    private String userId;
    private String botId;
    private String token;
    private String mmSiteUrlBase;
}
