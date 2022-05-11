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

@Service
public class SubscriptionKVServiceImpl implements SubscriptionKVService {

    private static final String FILE_KEY_PREFIX = "figma-file-";
    private static final String MM_CHANNEL_KEY_PREFIX = "mm-channel-";


    @Autowired
    private KVService kvService;

    @Autowired
    private JsonUtils jsonUtils;

    @Override
    public void putFile(FileData fileData, String mmChanelId, String mattermostSiteUrl, String token) {
        String fileKey = fileData.getFileKey();
        String fileName = fileData.getFileName();
        String subscribedBy = fileData.getSubscribedBy();
        mapChannelToFile(fileKey, mmChanelId, mattermostSiteUrl, token);
        mapFileToChannel(fileKey, fileName, subscribedBy, mmChanelId, mattermostSiteUrl, token);
    }

    private void mapFileToChannel(String fileKey, String fileName, String subscribedBy, String mmChannelId, String mattermostSiteUrl, String token) {
        String mmChanelSubscribedFiles = kvService.get(String.format("%s%s", MM_CHANNEL_KEY_PREFIX, mmChannelId), mattermostSiteUrl, token);
        Set<FileInfo> files = (Set<FileInfo>) jsonUtils.convertStringToObject(mmChanelSubscribedFiles, new TypeReference<Set<FileInfo>>() {
        }).orElse(new HashSet<FileInfo>());
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(fileKey);
        fileInfo.setFileName(fileName);
        fileInfo.setUserId(subscribedBy);
        fileInfo.setCreatedAt(LocalDate.now());
        files.add(fileInfo);
        kvService.put(String.format("%s%s", MM_CHANNEL_KEY_PREFIX, mmChannelId), files, mattermostSiteUrl, token);
    }

    private void mapChannelToFile(String fileKey, String mmChanelId, String mattermostSiteUrl, String token) {
        String mmSubscribedChannels = kvService.get(String.format("%s%s", FILE_KEY_PREFIX, fileKey), mattermostSiteUrl, token);
        Set<String> channels = (Set<String>) jsonUtils.convertStringToObject(mmSubscribedChannels, new TypeReference<Set<String>>() {
        }).orElse(new HashSet<String>());
        channels.add(mmChanelId);
        kvService.put(String.format("%s%s", FILE_KEY_PREFIX, fileKey), channels, mattermostSiteUrl, token);
    }

    @Override
    public Set<FileInfo> getFilesByMMChannelId(String mmChannelId, String mattermostSiteUrl, String token) {
        String mmChanelSubscribedFiles = kvService.get(String.format("%s%s", MM_CHANNEL_KEY_PREFIX, mmChannelId), mattermostSiteUrl, token);

        if (mmChanelSubscribedFiles.isBlank()) {
            return new HashSet<FileInfo>();
        }

        return (Set<FileInfo>) jsonUtils.convertStringToObject(mmChanelSubscribedFiles, new TypeReference<Set<FileInfo>>() {
        }).orElse(new HashSet<FileInfo>());
    }

    @Override
    public Set<String> getMMChannelIdsByFileId(String figmaFileId, String mattermostSiteUrl, String token) {
        String mmSubscribedChannels = kvService.get(String.format("%s%s", FILE_KEY_PREFIX, figmaFileId), mattermostSiteUrl, token);

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
        String mmChanelSubscribedFiles = kvService.get(String.format("%s%s", MM_CHANNEL_KEY_PREFIX, mmChannelId), mattermostSiteUrl, token);

        if (mmChanelSubscribedFiles.isBlank()) {
            return;
        }
        Set<FileInfo> files = (Set<FileInfo>) jsonUtils.convertStringToObject(mmChanelSubscribedFiles, new TypeReference<Set<FileInfo>>() {
        }).get();
        files.removeIf(f -> f.getFileId().equals(fileKey));
        kvService.put(String.format("%s%s", MM_CHANNEL_KEY_PREFIX, mmChannelId), files, mattermostSiteUrl, token);
    }

    private void removeChannelFromFile(String fileKey, String mmChannelId, String mattermostSiteUrl, String token) {
        String mmSubscribedChannelsToFile = kvService.get(String.format("%s%s", FILE_KEY_PREFIX, fileKey), mattermostSiteUrl, token);

        if (mmSubscribedChannelsToFile.isBlank()) {
            return;
        }

        Set<String> mmChannels = (Set<String>) jsonUtils.convertStringToObject(mmSubscribedChannelsToFile, new TypeReference<Set<String>>() {
        }).get();
        mmChannels.removeIf(channel -> channel.equals(mmChannelId));
        kvService.put(String.format("%s%s", FILE_KEY_PREFIX, fileKey), mmChannels, mattermostSiteUrl, token);
    }

}
