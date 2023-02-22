package com.mattermost.integration.figma.webhook.service;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import com.mattermost.integration.figma.api.figma.file.service.FigmaFileService;
import com.mattermost.integration.figma.api.figma.project.dto.ProjectDTO;
import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.api.figma.project.service.FigmaProjectService;
import com.mattermost.integration.figma.api.mm.kv.SubscriptionKVService;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.kv.dto.ProjectInfo;
import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FileCommentServiceImpl implements FileCommentService {

    @Autowired
    private SubscriptionKVService subscriptionKVService;
    @Autowired
    private FigmaProjectService figmaProjectService;
    @Autowired
    private FigmaFileService figmaFileService;

    @Override
    public void updateName(FileCommentWebhookResponse response) {
        String mmSiteUrl = response.getContext().getMattermostSiteUrl();
        String botAccessToken = response.getContext().getBotAccessToken();
        String fileKey = response.getValues().getData().getFileKey();

        updateProjectName(response, mmSiteUrl, botAccessToken, fileKey);
        updateFileName(response, mmSiteUrl, botAccessToken, fileKey);
    }

    private void updateProjectName(FileCommentWebhookResponse response, String mmSiteUrl, String botAccessToken, String fileKey) {
        String commenterId = response.getValues().getData().getTriggeredBy().getId();
        Optional<TeamProjectDTO> projects = figmaProjectService.getProjectsByTeamId(response);

        if (projects.isEmpty()) {
            return;
        }

        Optional<ProjectDTO> project = Optional.empty();
        for (ProjectDTO p : projects.get().getProjects()) {
            List<FigmaProjectFileDTO> projectFiles = figmaFileService.getProjectFiles(p.getId(), commenterId, mmSiteUrl, botAccessToken).get().getFiles();
            Optional<FigmaProjectFileDTO> fileDTO = projectFiles.stream().filter(pr -> fileKey.equals(pr.getKey())).findAny();
            if (fileDTO.isPresent()) {
                project = Optional.of(p);
                break;
            }
        }
        project.ifPresent(p -> updateProjectName(p, mmSiteUrl, botAccessToken));
    }

    private void updateFileName(FileCommentWebhookResponse response, String mmSiteUrl, String botAccessToken, String fileKey) {
        String fileName = response.getValues().getData().getFileName();

        Optional<FileInfo> file = subscriptionKVService.getFile(mmSiteUrl, botAccessToken, fileKey);
        file.ifPresent(f -> updateFile(f, mmSiteUrl, botAccessToken, fileName));
    }

    private void updateProjectName(ProjectDTO project, String mmSiteUrl, String botAccessToken) {
        String fileName = project.getName();
        Optional<ProjectInfo> projectInfo = subscriptionKVService.getProjectById(project.getId(), mmSiteUrl, botAccessToken);
        projectInfo.ifPresent(f -> updateProject(f, mmSiteUrl, botAccessToken, fileName));
    }

    private void updateFile(FileInfo fileInfo, String mmSiteUrl, String botAccessToken, String newFileName) {
        fileInfo.setFileName(newFileName);
        subscriptionKVService.updateFile(mmSiteUrl, botAccessToken, fileInfo);
    }

    private void updateProject(ProjectInfo projectInfo, String mmSiteUrl, String botAccessToken, String newProjectName) {
        projectInfo.setName(newProjectName);
        subscriptionKVService.updateProject(mmSiteUrl, botAccessToken, projectInfo);
    }
}
