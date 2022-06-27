package com.mattermost.integration.figma.subscribe.service;

import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;

import java.util.Set;

public interface SubscribeService {
    void subscribeToFile(InputPayload payload);

    void sendSubscriptionFilesToMMChannel(InputPayload payload);

    void unsubscribeFromFile(InputPayload payload, String fileKey);

    Set<String> getMMChannelIdsByFileId(Context context, String fileKey);

    Set<FileInfo> getFilesByChannelId(InputPayload request);

    boolean isBotExistsInChannel(InputPayload payload);

    boolean isBotExistsInTeam(InputPayload payload);

    Set<String> getMMChannelIdsByProjectId(Context context, String projectId);

    void unsubscribeFromProject(InputPayload payload, String projectId);

    void subscribeToProject(InputPayload payload);
}
