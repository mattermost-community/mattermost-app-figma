package com.mattermost.integration.figma.api.mm.dm.dto;

import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.input.mm.user.MMUser;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import lombok.Data;

@Data
public class FileSubscriptionMessage {
    private FileInfo fileInfo;
    private MMUser mmUser;
    private InputPayload payload;
}
