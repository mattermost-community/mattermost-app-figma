package com.mattermost.integration.figma.api.mm.dm.service;

import com.mattermost.integration.figma.api.mm.dm.dto.DMChannelPayload;
import com.mattermost.integration.figma.api.mm.dm.dto.DMMessagePayload;
import com.mattermost.integration.figma.input.oauth.InputPayload;

public interface DMMessageService {

    String createDMChannel(InputPayload payload);
    String createDMChannel(DMChannelPayload channelPayload);
    void sendDMMessage(DMMessagePayload messagePayload);

}
