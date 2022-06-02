package com.mattermost.integration.figma.api.mm.kv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.kv.dto.ProjectInfo;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMFileInfoNotFoundException;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMProjectInfoNotFoundException;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.subscribe.service.dto.FileData;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mattermost.integration.figma.constant.prefixes.subscription.SubscriptionPrefixes.*;

@Service
public class SubscriptionKVServiceImpl implements SubscriptionKVService {

    @Autowired
    private KVService kvService;

    @Autowired
    private JsonUtils jsonUtils;

    @Override
    public void putFile(FileData fileData, String mmChannelId, String mattermostSiteUrl, String token) {
        putFile(mattermostSiteUrl, token, fileData);

        kvService.addValuesToDoubleEndedKvPair(fileData.getFileKey(), mmChannelId, SUBSCRIPTION_FILE_KEY_PREFIX,
                SUBSCRIPTION_MM_CHANNEL_KEY_PREFIX, mattermostSiteUrl, token);
    }

    @Override
    public Optional<FileInfo> getFile(String mattermostSiteUrl, String token, String id) {
        String file = kvService.get(String.format("%s%s", FILE_KEY_PREFIX, id), mattermostSiteUrl, token);
        if (file.isBlank()) {
            return Optional.empty();
        }
        return Optional.of((FileInfo) jsonUtils.convertStringToObject(file, new TypeReference<FileInfo>() {
        }).orElse(Optional.empty()));
    }

    @Override
    public void updateFile(String mattermostSiteUrl, String token, FileInfo fileInfo) {
        kvService.put(String.format("%s%s", FILE_KEY_PREFIX, fileInfo.getFileId()), fileInfo, mattermostSiteUrl, token);
    }

    private void putFile(String mattermostSiteUrl, String token, FileData fileData) {
        String fileKey = fileData.getFileKey();

        String fileString = kvService.get(String.format("%s%s", FILE_KEY_PREFIX, fileKey), mattermostSiteUrl, token);
        FileInfo fileInfo = (FileInfo) jsonUtils.convertStringToObject(fileString, new TypeReference<FileInfo>() {
        }).orElse(new FileInfo());

        fileInfo.setFileId(fileKey);
        fileInfo.setFileName(fileData.getFileName());
        fileInfo.setUserId(fileData.getSubscribedBy());
        fileInfo.setCreatedAt(LocalDate.now());
        fileInfo.setFigmaUserId(fileData.getFigmaUserId());

        kvService.put(String.format("%s%s", FILE_KEY_PREFIX, fileKey), fileInfo, mattermostSiteUrl, token);
    }

    @Override
    public Set<FileInfo> getFilesByMMChannelId(String mmChannelId, String mattermostSiteUrl, String token) {
        String mmChanelSubscribedFiles = kvService.get(String.format("%s%s", SUBSCRIPTION_MM_CHANNEL_KEY_PREFIX, mmChannelId), mattermostSiteUrl, token);

        if (mmChanelSubscribedFiles.isBlank()) {
            return new HashSet<FileInfo>();
        }

        Set<String> fileIds = (Set<String>) jsonUtils.convertStringToObject(mmChanelSubscribedFiles, new TypeReference<Set<String>>() {
        }).orElse(new HashSet<String>());

        return fileIds.stream().map(id -> getFile(mattermostSiteUrl, token, id).orElseThrow(() -> new MMFileInfoNotFoundException(id))).collect(Collectors.toSet());
    }


    @Override
    public Set<String> getMMChannelIdsByFileId(String figmaFileId, String mattermostSiteUrl, String token) {
        return kvService.getSetFromKv(figmaFileId, mattermostSiteUrl, token, SUBSCRIPTION_FILE_KEY_PREFIX);
    }

    @Override
    public void unsubscribeFileFromChannel(String fileKey, String mmChannelId, String mattermostSiteUrl, String token) {
        kvService.deleteValuesFromDoubleEndedKvPair(fileKey, mmChannelId, SUBSCRIPTION_FILE_KEY_PREFIX, SUBSCRIPTION_MM_CHANNEL_KEY_PREFIX, mattermostSiteUrl, token);
    }

    @Override
    public Optional<ProjectInfo> getProjectById(String projectId, String mattermostSiteUrl, String token) {
        String projectString = kvService.get(PROJECT_KEY_PREFIX.concat(projectId), mattermostSiteUrl, token);
        if (projectString.isBlank()) {
            return Optional.empty();
        }
        return Optional.of((ProjectInfo) jsonUtils.convertStringToObject(projectString, ProjectInfo.class).orElse(Optional.empty()));
    }

    @Override
    public Set<String> getMMChannelIdsByProjectId(String projectId, String mattermostSiteUrl, String token) {
        return kvService.getSetFromKv(projectId, mattermostSiteUrl, token, SUBSCRIPTION_PROJECT_TO_MM_CHANNEL_PREFIX);
    }

    @Override
    public Set<String> getProjectIdsByChannelId(String channelId, String mattermostSiteUrl, String token) {
        return kvService.getSetFromKv(channelId, mattermostSiteUrl, token, SUBSCRIPTION_MM_CHANNEL_TO_PROJECT_PREFIX);
    }

    @Override
    public Set<ProjectInfo> getProjectsByMMChannelId(String channelId, String mattermostSiteUrl, String token) {
        Set<String> projectIds = getProjectIdsByChannelId(channelId, mattermostSiteUrl, token);
        return projectIds.stream().map(projectId -> getProjectById(projectId, mattermostSiteUrl, token)
                .orElseThrow(() -> new MMProjectInfoNotFoundException(projectId))).collect(Collectors.toSet());
    }

    @Override
    public void putProject(InputPayload payload) {

        String channelId = payload.getContext().getChannel().getId();
        String mmSiteUrl = payload.getContext().getMattermostSiteUrl();
        String botAccessToken = payload.getContext().getBotAccessToken();
        String projectId = payload.getValues().getProject().getValue();
        String projectName = payload.getValues().getProject().getLabel();
        String subscribedBy = payload.getContext().getActingUser().getId();
        String figmaUserId = payload.getContext().getOauth2().getUser().getUserId();

        String projectString = kvService.get(String.format("%s%s", PROJECT_KEY_PREFIX, projectId), mmSiteUrl, botAccessToken);
        ProjectInfo projectInfo = (ProjectInfo) jsonUtils.convertStringToObject(projectString, ProjectInfo.class).orElse(new ProjectInfo());

        projectInfo.setProjectId(projectId);
        projectInfo.setName(projectName);
        projectInfo.setUserId(subscribedBy);
        projectInfo.setCreatedAt(LocalDate.now());
        projectInfo.setFigmaUserId(figmaUserId);

        kvService.put(String.format("%s%s", PROJECT_KEY_PREFIX, projectId), projectInfo, mmSiteUrl, botAccessToken);
        kvService.addValuesToDoubleEndedKvPair(projectId, channelId, SUBSCRIPTION_PROJECT_TO_MM_CHANNEL_PREFIX, SUBSCRIPTION_MM_CHANNEL_TO_PROJECT_PREFIX, mmSiteUrl, botAccessToken);
    }

    @Override
    public void updateProject(String mattermostSiteUrl, String token, ProjectInfo projectInfo) {
        kvService.put(String.format("%s%s", PROJECT_KEY_PREFIX, projectInfo.getProjectId()), projectInfo, mattermostSiteUrl, token);

    }

    @Override
    public void unsubscribeProjectFromChannel(String projectId, String mmChannelId, String mattermostSiteUrl, String token) {
        kvService.deleteValuesFromDoubleEndedKvPair(projectId, mmChannelId, SUBSCRIPTION_PROJECT_TO_MM_CHANNEL_PREFIX, SUBSCRIPTION_MM_CHANNEL_TO_PROJECT_PREFIX, mattermostSiteUrl, token);
    }
}
