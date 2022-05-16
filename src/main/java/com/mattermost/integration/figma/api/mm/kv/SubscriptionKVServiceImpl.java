package com.mattermost.integration.figma.api.mm.kv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.subscribe.service.dto.FileData;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubscriptionKVServiceImpl implements SubscriptionKVService {

    private static final String SUBSCRIPTION_FILE_KEY_PREFIX = "subscription-figma-file-";
    private static final String SUBSCRIPTION_MM_CHANNEL_KEY_PREFIX = "subscription-mm-channel-";
    private static final String FILE_KEY_PREFIX = "figma-file-";


    @Autowired
    private KVService kvService;

    @Autowired
    private JsonUtils jsonUtils;

    @Override
    public void putFile(FileData fileData, String mmChanelId, String mattermostSiteUrl, String token) {
        String fileKey = fileData.getFileKey();
        String fileName = fileData.getFileName();
        String subscribedBy = fileData.getSubscribedBy();

        putFile(mattermostSiteUrl, token, fileKey, fileName, subscribedBy);

        mapChannelToFile(fileKey, mmChanelId, mattermostSiteUrl, token);
        mapFileToChannel(fileKey, mmChanelId, mattermostSiteUrl, token);
    }

    private void putFile(String mattermostSiteUrl, String token, String fileKey, String fileName, String subscribedBy) {
        String fileString = kvService.get(String.format("%s%s", FILE_KEY_PREFIX, fileKey), mattermostSiteUrl, token);
        FileInfo fileInfo = (FileInfo) jsonUtils.convertStringToObject(fileString, new TypeReference<FileInfo>() {
        }).orElse(new FileInfo());

        fileInfo.setFileId(fileKey);
        fileInfo.setFileName(fileName);
        fileInfo.setUserId(subscribedBy);
        fileInfo.setCreatedAt(LocalDate.now());

        kvService.put(String.format("%s%s", FILE_KEY_PREFIX, fileKey), fileInfo, mattermostSiteUrl, token);
    }

    private void mapFileToChannel(String fileKey, String mmChannelId, String mattermostSiteUrl, String token) {
        String mmChanelSubscribedFiles = kvService.get(String.format("%s%s", SUBSCRIPTION_MM_CHANNEL_KEY_PREFIX, mmChannelId), mattermostSiteUrl, token);
        Set<String> files = (Set<String>) jsonUtils.convertStringToObject(mmChanelSubscribedFiles, new TypeReference<Set<String>>() {
        }).orElse(new HashSet<String>());

        files.add(fileKey);
        kvService.put(String.format("%s%s", SUBSCRIPTION_MM_CHANNEL_KEY_PREFIX, mmChannelId), files, mattermostSiteUrl, token);
    }

    private void mapChannelToFile(String fileKey, String mmChanelId, String mattermostSiteUrl, String token) {
        String mmSubscribedChannels = kvService.get(String.format("%s%s", SUBSCRIPTION_FILE_KEY_PREFIX, fileKey), mattermostSiteUrl, token);
        Set<String> channels = (Set<String>) jsonUtils.convertStringToObject(mmSubscribedChannels, new TypeReference<Set<String>>() {
        }).orElse(new HashSet<String>());
        channels.add(mmChanelId);
        kvService.put(String.format("%s%s", SUBSCRIPTION_FILE_KEY_PREFIX, fileKey), channels, mattermostSiteUrl, token);
    }

    @Override
    public Set<FileInfo> getFilesByMMChannelId(String mmChannelId, String mattermostSiteUrl, String token) {
        String mmChanelSubscribedFiles = kvService.get(String.format("%s%s", SUBSCRIPTION_MM_CHANNEL_KEY_PREFIX, mmChannelId), mattermostSiteUrl, token);

        if (mmChanelSubscribedFiles.isBlank()) {
            return new HashSet<FileInfo>();
        }

        Set<String> fileIds = (Set<String>) jsonUtils.convertStringToObject(mmChanelSubscribedFiles, new TypeReference<Set<String>>() {
        }).orElse(new HashSet<String>());

        return fileIds.stream().map(id -> getFile(mattermostSiteUrl, token, id)).collect(Collectors.toSet());
    }

    private FileInfo getFile(String mattermostSiteUrl, String token, String id) {
        String file = kvService.get(String.format("%s%s", FILE_KEY_PREFIX, id), mattermostSiteUrl, token);
        if (file.isBlank()) {
            return new FileInfo();
        }
        return (FileInfo) jsonUtils.convertStringToObject(file, new TypeReference<FileInfo>() {
        }).orElse(new FileInfo());
    }

    @Override
    public Set<String> getMMChannelIdsByFileId(String figmaFileId, String mattermostSiteUrl, String token) {
        String mmSubscribedChannels = kvService.get(String.format("%s%s", SUBSCRIPTION_FILE_KEY_PREFIX, figmaFileId), mattermostSiteUrl, token);

        if (mmSubscribedChannels.isBlank()) {
            return new HashSet<String>();
        }

        return (Set<String>) jsonUtils.convertStringToObject(mmSubscribedChannels, new TypeReference<Set<String>>() {
        }).orElse(new HashSet<String>());
    }

    @Override
    public void unsubscribeFileFromChannel(String fileKey, String mmChannelId, String mattermostSiteUrl, String token) {
        removeFileFromChannel(fileKey, mmChannelId, mattermostSiteUrl, token);
        removeChannelFromFile(fileKey, mmChannelId, mattermostSiteUrl, token);
    }

    private void removeFileFromChannel(String fileKey, String mmChannelId, String mattermostSiteUrl, String token) {
        String mmChanelSubscribedFiles = kvService.get(String.format("%s%s", SUBSCRIPTION_MM_CHANNEL_KEY_PREFIX, mmChannelId), mattermostSiteUrl, token);

        if (mmChanelSubscribedFiles.isBlank()) {
            return;
        }
        Set<String> files = (Set<String>) jsonUtils.convertStringToObject(mmChanelSubscribedFiles, new TypeReference<Set<String>>() {
        }).get();
        files.removeIf(s -> s.equals(fileKey));
        kvService.put(String.format("%s%s", SUBSCRIPTION_MM_CHANNEL_KEY_PREFIX, mmChannelId), files, mattermostSiteUrl, token);
    }

    private void removeChannelFromFile(String fileKey, String mmChannelId, String mattermostSiteUrl, String token) {
        String mmSubscribedChannelsToFile = kvService.get(String.format("%s%s", SUBSCRIPTION_FILE_KEY_PREFIX, fileKey), mattermostSiteUrl, token);

        if (mmSubscribedChannelsToFile.isBlank()) {
            return;
        }

        Set<String> mmChannels = (Set<String>) jsonUtils.convertStringToObject(mmSubscribedChannelsToFile, new TypeReference<Set<String>>() {
        }).get();
        mmChannels.removeIf(channel -> channel.equals(mmChannelId));
        kvService.put(String.format("%s%s", SUBSCRIPTION_FILE_KEY_PREFIX, fileKey), mmChannels, mattermostSiteUrl, token);
    }

}
