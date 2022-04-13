package com.mattermost.integration.figma.api.mm.dm.dto;

import com.mattermost.integration.figma.input.mm.form.DMFormMessageReply;
import lombok.Data;

@Data
public class DMMessageWithPropsPayload {
    private DMFormMessageReply body;
    private String token;
    private String mmSiteUrl;
}
