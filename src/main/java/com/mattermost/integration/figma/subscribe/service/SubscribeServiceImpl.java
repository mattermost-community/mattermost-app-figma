package com.mattermost.integration.figma.subscribe.service;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.api.figma.file.service.FigmaFileService;
import com.mattermost.integration.figma.api.figma.project.dto.ProjectDTO;
import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.api.figma.project.service.FigmaProjectService;
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
import com.mattermost.integration.figma.input.mm.user.MMTeamUser;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.UserDataDto;
import com.mattermost.integration.figma.security.service.OAuthService;
import com.mattermost.integration.figma.subscribe.service.dto.FileData;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mattermost.integration.figma.constant.prefixes.user.UserPrefixes.MM_USER_ID_PREFIX;

@Service
@Slf4j
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

    @Autowired
    private FigmaProjectService figmaProjectService;

    @Autowired
    private OAuthService oAuthService;

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
        try {
            updateProjects(projects, mattermostSiteUrl, botAccessToken);
            updateFiles(files, mattermostSiteUrl, botAccessToken);
        } catch (RestClientException e) {
            log.error(e.getMessage());
        } finally {
            sendProjectAndFileSubscriptionsToMM(payload, projects, files);
        }
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

    @Override
    public boolean isBotExistsInTeam(InputPayload payload) {
        String userAccessToken = payload.getContext().getActingUserAccessToken();
        String teamId = payload.getContext().getChannel().getTeamId();
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        String botUserId = payload.getContext().getBotUserId();

        List<MMTeamUser> usersInTeam = mmUserService.getUsersByTeamId(teamId, mattermostSiteUrl, userAccessToken);

        return usersInTeam.stream().anyMatch(u -> botUserId.equals(u.getUserID()));
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
        Optional<FigmaProjectFilesDTO> projectFiles = figmaFileService.getProjectFiles(projectId, figmaUserId, mattermostSiteUrl, botAccessToken);
        if (projectFiles.isEmpty()) {
            return;
        }
        Set<String> projectFilesIds = projectFiles.get().getFiles().stream().map(FigmaProjectFileDTO::getKey).collect(Collectors.toSet());
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
            Optional<FigmaProjectFilesDTO> projectFiles = figmaFileService.getProjectFiles(projectInfo.getProjectId(), figmaUserId, mattermostSiteUrl, botAccessToken);
            if (projectFiles.isEmpty()) {
                continue;
            }
            Set<String> projectFilesIds = projectFiles.get().getFiles().stream().map(FigmaProjectFileDTO::getKey).collect(Collectors.toSet());
            if (projectFilesIds.contains(file.getValue())) {
                throw new MMSubscriptionToFileInSubscribedProjectException(projectInfo.getName());
            }
        }
    }

    private void updateProjects(Set<ProjectInfo> projects, String mmSiteUrl, String botAccessToken) {
        for (ProjectInfo project : projects) {

            Optional<TeamProjectDTO> projectsByTeamIdOptional = figmaProjectService.getProjectsByTeamId(project.getTeamId(),
                    project.getFigmaUserId(), mmSiteUrl, botAccessToken);

            if (projectsByTeamIdOptional.isEmpty()) {
                continue;
            }

            List<ProjectDTO> figmaProjects = projectsByTeamIdOptional.get().getProjects();
            figmaProjects.stream().filter(projectDTO -> projectDTO.getId().equals(project.getProjectId()))
                    .forEach(projectDTO -> {
                        subscriptionKVService.updateProjectName(projectDTO.getName(),
                                projectDTO.getId(), mmSiteUrl, botAccessToken);
                        project.setName(projectDTO.getName());
                    });
        }
    }

    private void updateFiles(Set<FileInfo> files, String mmSiteUrl, String botAccessToken) {
        for (FileInfo fileInfo : files) {
            Optional<UserDataDto> userDataOptional = userDataKVService.getUserData(fileInfo.getFigmaUserId(), mmSiteUrl, botAccessToken);

            if (userDataOptional.isEmpty()) {
                continue;
            }

            UserDataDto userDataDto = userDataOptional.get();
            String accessToken = oAuthService.refreshToken(userDataDto.getClientId(), userDataDto.getClientSecret(), userDataDto.getRefreshToken()).getAccessToken();
            FigmaProjectFileDTO upToDateFile = figmaFileService.getFileByKey(fileInfo.getFileId(), accessToken);
            subscriptionKVService.updateFileName(upToDateFile.getName(), upToDateFile.getKey(), mmSiteUrl, botAccessToken);
            fileInfo.setFileName(upToDateFile.getName());
        }
    }

    private void sendProjectAndFileSubscriptionsToMM(InputPayload payload, Set<ProjectInfo> projects, Set<FileInfo> files) {
        if (files.isEmpty() && projects.isEmpty()) {
            dmMessageSenderService.sendMessage(payload, "You have no subscriptions in this channel");
            return;
        }
        files.forEach(f -> dmMessageSenderService.sendFileSubscriptionToMMChat(f, payload));
        projects.forEach(project -> dmMessageSenderService.sendProjectSubscriptionsToMMChat(project, payload));
    }
}
