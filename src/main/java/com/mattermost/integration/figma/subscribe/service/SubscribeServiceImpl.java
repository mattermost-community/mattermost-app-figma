package com.mattermost.integration.figma.subscribe.service;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import com.mattermost.integration.figma.api.figma.file.service.FigmaFileService;
import com.mattermost.integration.figma.api.mm.dm.service.DMMessageSenderService;
import com.mattermost.integration.figma.api.mm.kv.KVService;
import com.mattermost.integration.figma.api.mm.kv.SubscriptionKVService;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.kv.dto.ProjectInfo;
import com.mattermost.integration.figma.api.mm.user.MMUserService;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMSubscriptionToFileInSubscribedProjectException;
import com.mattermost.integration.figma.input.mm.form.MMStaticSelectField;
import com.mattermost.integration.figma.input.mm.user.MMChannelUser;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.subscribe.service.dto.FileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mattermost.integration.figma.constant.prefixes.user.UserPrefixes.MM_USER_ID_PREFIX;

@Service
public class SubscribeServiceImpl implements SubscribeService {

    @Autowired
    private SubscriptionKVService subscriptionKVService;

    @Autowired
    private DMMessageSenderService dmMessageSenderService;

    @Autowired
    private MMUserService mmUserService;

    @Autowired
    private FigmaFileService figmaFileService;

    @Autowired
    private KVService kvService;

    @Autowired
    private UserDataKVService userDataKVService;

    @Override
    public void subscribeToFile(InputPayload payload) {
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        MMStaticSelectField file = payload.getValues().getFile();
        String mmChannelID = payload.getContext().getChannel().getId();
        String botAccessToken = payload.getContext().getBotAccessToken();
        String figmaUserId = payload.getContext().getOauth2().getUser().getUserId();
        FileData fileData = new FileData();
        fileData.setFileKey(file.getValue());
        fileData.setFigmaUserId(figmaUserId);
        fileData.setFileName(file.getLabel());
        fileData.setSubscribedBy(payload.getContext().getActingUser().getId());
        checkIfFileIsNotInSubscribedProjects(payload);
        subscriptionKVService.putFile(fileData, mmChannelID, mattermostSiteUrl, botAccessToken);
    }

    @Override
    public void sendSubscriptionFilesToMMChannel(InputPayload payload) {
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String mmChannelID = payload.getContext().getChannel().getId();
        String botAccessToken = payload.getContext().getBotAccessToken();
        Set<FileInfo> files = subscriptionKVService.getFilesByMMChannelId(mmChannelID, mattermostSiteUrl, botAccessToken);
        Set<ProjectInfo> projects = subscriptionKVService.getProjectsByMMChannelId(mmChannelID, mattermostSiteUrl, botAccessToken);
        if (files.isEmpty() && projects.isEmpty()) {
            dmMessageSenderService.sendMessage(payload, "You have no subscriptions in this channel");
            return;
        }
        files.forEach(f -> dmMessageSenderService.sendFileSubscriptionToMMChat(f, payload));
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
        subscriptionKVService.putProject(payload);
        checkIfProjectHasSubscribedFiles(payload);
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

    private void checkIfProjectHasSubscribedFiles(InputPayload payload) {
        String channelId = payload.getContext().getChannel().getId();
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String botAccessToken = payload.getContext().getBotAccessToken();
        String projectId = payload.getValues().getProject().getValue();
        String mmUserId = payload.getContext().getActingUser().getId();

        String figmaUserId = kvService.get(MM_USER_ID_PREFIX.concat(mmUserId), mattermostSiteUrl, botAccessToken);
        Set<String> projectFilesIds = figmaFileService.getProjectFiles(projectId, figmaUserId, mattermostSiteUrl, botAccessToken)
                .getFiles().stream().map(FigmaProjectFileDTO::getKey).collect(Collectors.toSet());
        Set<String> channelSubscribedFilesIds = subscriptionKVService.getFilesByMMChannelId(channelId, mattermostSiteUrl,
                botAccessToken).stream().map(FileInfo::getFileId).collect(Collectors.toSet());
        for (String projectFileId : projectFilesIds) {
            if (channelSubscribedFilesIds.contains(projectFileId)) {
                unsubscribeFromFile(payload, projectFileId);
            }
        }
    }

    private void checkIfFileIsNotInSubscribedProjects(InputPayload payload) {
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        MMStaticSelectField file = payload.getValues().getFile();
        String mmChannelID = payload.getContext().getChannel().getId();
        String botAccessToken = payload.getContext().getBotAccessToken();
        String mmUserId = payload.getContext().getActingUser().getId();

        Set<ProjectInfo> channelProjects = subscriptionKVService.getProjectsByMMChannelId(mmChannelID, mattermostSiteUrl, botAccessToken);
        String figmaUserId = kvService.get(MM_USER_ID_PREFIX.concat(mmUserId), mattermostSiteUrl, botAccessToken);
        for (ProjectInfo projectInfo : channelProjects) {
            Set<String> projectFilesIds = figmaFileService.getProjectFiles(projectInfo.getProjectId(), figmaUserId, mattermostSiteUrl, botAccessToken)
                    .getFiles().stream().map(FigmaProjectFileDTO::getKey).collect(Collectors.toSet());
            if (projectFilesIds.contains(file.getValue())) {
                throw new MMSubscriptionToFileInSubscribedProjectException(projectInfo.getName());
            }
        }
    }
}
