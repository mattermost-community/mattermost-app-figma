package com.mattermost.integration.figma.subscribe.service;

import com.mattermost.integration.figma.api.mm.dm.service.DMMessageSenderService;
import com.mattermost.integration.figma.api.mm.kv.SubscriptionKVService;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.input.mm.form.MMStaticSelectField;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.subscribe.service.dto.FileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SubscribeServiceImpl implements SubscribeService {

    @Autowired
    private SubscriptionKVService subscriptionKVService;

    @Autowired
    private DMMessageSenderService dmMessageSenderService;

    @Override
    public void subscribe(InputPayload payload) {
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        MMStaticSelectField file = payload.getValues().getFile();
        String mmChannelID = payload.getContext().getChannel().getId();
        String botAccessToken = payload.getContext().getBotAccessToken();
        FileData fileData = new FileData();
        fileData.setFileKey(file.getValue());
        fileData.setFileName(file.getLabel());
        fileData.setSubscribedBy(payload.getContext().getActingUser().getId());

        subscriptionKVService.putFile(fileData, mmChannelID, mattermostSiteUrl, botAccessToken);
    }

    @Override
    public void sendSubscriptionFilesToMMChannel(InputPayload payload) {
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String mmChannelID = payload.getContext().getChannel().getId();
        String botAccessToken = payload.getContext().getBotAccessToken();
        Set<FileInfo> files = subscriptionKVService.getFilesByMMChannelId(mmChannelID, mattermostSiteUrl, botAccessToken);
        files.forEach(f -> dmMessageSenderService.sendFileSubscriptionToMMChat(f, payload));
    }

    @Override
    public void unsubscribe(InputPayload payload, String fileKey) {
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String mmChannelID = payload.getContext().getChannel().getId();
        String botAccessToken = payload.getContext().getBotAccessToken();
        subscriptionKVService.unsubscribeFileFromChannel(fileKey, mmChannelID, mattermostSiteUrl , botAccessToken);
    }
}
