package com.mattermost.integration.figma.api.mm.kv;

import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.subscribe.service.dto.FileData;

import java.util.Set;

public interface SubscriptionKVService {

    void putFile(FileData fileData, String mmChanelId, String mattermostSiteUrl, String token);
    Set<FileInfo> getFilesByMMChannelId(String mmChannelId, String mattermostSiteUrl, String token);
    Set<String> getMMChannelIdsByFileId(String figmaFileId, String mattermostSiteUrl, String token);
    void unsubscribeFileFromChannel(String fileKey, String mmChannelId, String mattermostSiteUrl, String token);
}
