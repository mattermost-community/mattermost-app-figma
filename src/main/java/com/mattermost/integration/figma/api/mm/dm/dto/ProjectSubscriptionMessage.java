package com.mattermost.integration.figma.api.mm.dm.dto;

import com.mattermost.integration.figma.api.mm.kv.dto.ProjectInfo;
import com.mattermost.integration.figma.input.mm.user.MMUser;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import lombok.Data;

@Data
public class ProjectSubscriptionMessage {
    private ProjectInfo projectInfo;
    private MMUser mmUser;
    private InputPayload payload;
}
