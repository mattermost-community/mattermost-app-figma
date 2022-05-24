package com.mattermost.integration.figma.webhook.service;

import com.mattermost.integration.figma.api.mm.kv.SubscriptionKVService;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FileCommentServiceImpl implements FileCommentService {

    @Autowired
    private SubscriptionKVService subscriptionKVService;

    @Override
    public void updateName(FileCommentWebhookResponse response) {
        String mmSiteUrl = response.getContext().getMattermostSiteUrl();
        String botAccessToken = response.getContext().getBotAccessToken();
        String fileKey = response.getValues().getData().getFileKey();
        String fileName = response.getValues().getData().getFileName();

        Optional<FileInfo> file = subscriptionKVService.getFile(mmSiteUrl, botAccessToken, fileKey);
        file.ifPresent(f -> updateFile(f, mmSiteUrl, botAccessToken, fileName));
    }

    private void updateFile(FileInfo fileInfo, String mmSiteUrl, String botAccessToken, String newFileName) {
        fileInfo.setFileName(newFileName);
        subscriptionKVService.updateFile(mmSiteUrl, botAccessToken, fileInfo);
    }
}
