package com.mattermost.integration.figma.subscribe.service;

import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;

import java.util.Set;

public interface SubscribeService {
    void subscribe(InputPayload payload);
    void sendSubscriptionFilesToMMChannel(InputPayload payload);
    void unsubscribe(InputPayload payload, String fileKey);
    Set<String> getMMChannelIdsByFileId(Context context, String fileKey);
    Set<FileInfo> getFilesByChannelId(InputPayload request);
    boolean isBotExistsInChannel(InputPayload payload);


}
