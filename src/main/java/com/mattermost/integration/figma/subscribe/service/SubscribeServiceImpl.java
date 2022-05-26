package com.mattermost.integration.figma.subscribe.service;

import com.mattermost.integration.figma.api.mm.dm.service.DMMessageSenderService;
import com.mattermost.integration.figma.api.mm.kv.SubscriptionKVService;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.kv.dto.ProjectInfo;
import com.mattermost.integration.figma.api.mm.user.MMUserService;
import com.mattermost.integration.figma.input.mm.form.MMStaticSelectField;
import com.mattermost.integration.figma.input.mm.user.MMChannelUser;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.subscribe.service.dto.FileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SubscribeServiceImpl implements SubscribeService {

    @Autowired
    private SubscriptionKVService subscriptionKVService;

    @Autowired
    private DMMessageSenderService dmMessageSenderService;

    @Autowired
    private MMUserService mmUserService;

    @Override
    public void subscribeToFile(InputPayload payload) {
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
        if (files.isEmpty()) {
            dmMessageSenderService.sendMessage(payload, "You have no subscriptions in this channel");
            return;
        }
        files.forEach(f -> dmMessageSenderService.sendFileSubscriptionToMMChat(f, payload));
        Set<ProjectInfo> projects = subscriptionKVService.getProjectsByMMChannelId(mmChannelID, mattermostSiteUrl, botAccessToken);
        projects.forEach(project -> dmMessageSenderService.sendProjectSubscriptionsToMMChat(project, payload));
    }

    @Override
    public void unsubscribeFromFile(InputPayload payload, String fileKey) {
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String mmChannelID = payload.getContext().getChannel().getId();
        String botAccessToken = payload.getContext().getBotAccessToken();
        subscriptionKVService.unsubscribeFileFromChannel(fileKey, mmChannelID, mattermostSiteUrl, botAccessToken);
    }

    @Override
    public Set<String> getMMChannelIdsByFileId(Context context, String fileKey) {
        String mattermostSiteUrl = context.getMattermostSiteUrl();
        String botAccessToken = context.getBotAccessToken();
        return subscriptionKVService.getMMChannelIdsByFileId(fileKey, mattermostSiteUrl, botAccessToken);
    }

    @Override
    public Set<FileInfo> getFilesByChannelId(InputPayload request) {
        String mattermostSiteUrl = request.getContext().getMattermostSiteUrl();
        String botAccessToken = request.getContext().getBotAccessToken();
        String channelId = request.getContext().getChannel().getId();
        return subscriptionKVService.getFilesByMMChannelId(channelId, mattermostSiteUrl, botAccessToken);
    }

    @Override
    public boolean isBotExistsInChannel(InputPayload payload) {
        String userAccessToken = payload.getContext().getActingUserAccessToken();
        String channelId = payload.getContext().getChannel().getId();
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String botUserId = payload.getContext().getBotUserId();

        List<MMChannelUser> usersInChannel = mmUserService.getUsersByChannelId(channelId, mattermostSiteUrl, userAccessToken);

        return usersInChannel.stream().anyMatch(u -> botUserId.equals(u.getUserId()));
    }

    public void subscribeToProject(InputPayload payload) {
        String channelId = payload.getContext().getChannel().getId();
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String botAccessToken = payload.getContext().getBotAccessToken();
        String projectId = payload.getValues().getProject().getValue();
        String projectName = payload.getValues().getProject().getLabel();
        String subscribedBy = payload.getContext().getActingUser().getId();

        subscriptionKVService.putProject(projectId, projectName, subscribedBy, channelId, mattermostSiteUrl, botAccessToken);
    }


    public void unsubscribeFromProject(InputPayload payload, String projectId) {
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String mmChannelID = payload.getContext().getChannel().getId();
        String botAccessToken = payload.getContext().getBotAccessToken();
        subscriptionKVService.unsubscribeProjectFromChannel(projectId, mmChannelID, mattermostSiteUrl, botAccessToken);
    }

    public Set<String> getMMChannelIdsByProjectId(Context context, String projectId) {
        String mattermostSiteUrl = context.getMattermostSiteUrl();
        String botAccessToken = context.getBotAccessToken();
        return subscriptionKVService.getMMChannelIdsByProjectId(projectId, mattermostSiteUrl, botAccessToken);
    }
}
