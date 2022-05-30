package com.mattermost.integration.figma.api.mm.kv;

import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.kv.dto.ProjectInfo;
import com.mattermost.integration.figma.subscribe.service.dto.FileData;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionKVService {

    Optional<FileInfo> getFile(String mattermostSiteUrl, String token, String id);

    void updateFile(String mattermostSiteUrl, String token, FileInfo fileInfo);

    void putFile(FileData fileData, String mmChanelId, String mattermostSiteUrl, String token);

    Set<FileInfo> getFilesByMMChannelId(String mmChannelId, String mattermostSiteUrl, String token);

    Set<String> getMMChannelIdsByFileId(String figmaFileId, String mattermostSiteUrl, String token);

    void unsubscribeFileFromChannel(String fileKey, String mmChannelId, String mattermostSiteUrl, String token);

    void unsubscribeProjectFromChannel(String projectId, String mmChannelId, String mattermostSiteUrl, String token);

    void putProject(String projectId, String projectName, String subscribedBy, String channelId, String mmSiteUrl, String botAccessToken);

    void updateProject(String mattermostSiteUrl, String token, ProjectInfo projectInfo);

    Set<ProjectInfo> getProjectsByMMChannelId(String channelId, String mattermostSiteUrl, String token);

    Set<String> getProjectIdsByChannelId(String channelId, String mattermostSiteUrl, String token);

    Set<String> getMMChannelIdsByProjectId(String projectId, String mattermostSiteUrl, String token);

    Optional<ProjectInfo> getProjectById(String projectId, String mattermostSiteUrl, String token);
}
