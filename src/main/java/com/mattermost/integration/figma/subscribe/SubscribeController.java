package com.mattermost.integration.figma.subscribe;


import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.api.figma.file.service.FigmaFileService;
import com.mattermost.integration.figma.api.figma.notification.service.FileNotificationService;
import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.api.figma.project.service.FigmaProjectService;
import com.mattermost.integration.figma.api.mm.dm.component.FigmaFilesFormReplyCreator;
import com.mattermost.integration.figma.api.mm.dm.component.ProjectFormReplyCreator;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.user.MMUserService;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMSubscriptionFromDMChannelException;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMSubscriptionInChannelWithoutBotException;
import com.mattermost.integration.figma.input.mm.form.FormType;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.subscribe.service.SubscribeService;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@RestController
@Slf4j
public class SubscribeController {
    @Autowired
    private FileNotificationService fileNotificationService;
    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private UserDataKVService userDataKVService;
    @Autowired
    private FigmaProjectService figmaProjectService;
    @Autowired
    private FigmaFileService figmaFileService;

    @PostMapping("/subscribe")
    public FormType subscribeToFileComment(@RequestBody InputPayload request) {

        if ("D".equalsIgnoreCase(request.getContext().getChannel().getType())) {
            throw new MMSubscriptionFromDMChannelException();
        }

        if (!subscribeService.isBotExistsInChannel(request)) {
            throw new MMSubscriptionInChannelWithoutBotException();
        }

        System.out.println(request);
        log.info("Subscription to file comment from user with id: " + request.getContext().getUserAgent() + " has come");
        log.debug("Subscription to file comment request: " + request);

        TeamProjectDTO projects = figmaProjectService.getProjectsByTeamId(request);
        String teamId = request.getValues().getTeamId();
        ProjectFormReplyCreator projectFormReplyCreator = new ProjectFormReplyCreator();

        return projectFormReplyCreator.create(projects, teamId);
    }

    @PostMapping("{teamId}/projects")
    public Object sendProjectFiles(@RequestBody InputPayload request, @PathVariable String teamId) {
        System.out.println(request);
        if (request.getValues().getIsProjectSubscription().equals("true")) {
            request.getValues().setTeamId(teamId);
            fileNotificationService.subscribeToFileNotification(request);
            userDataKVService.saveUserData(request);
            subscribeService.subscribeToProject(request);
            return "{\"text\":\"Subscribed\"}";
        }

        log.info("Get files for project: " + request.getValues().getProject().getValue() + " has come");
        log.debug("Get files for project request: " + request);

        FigmaProjectFilesDTO projectFiles = figmaFileService.getProjectFiles(request);

        FigmaFilesFormReplyCreator figmaFilesFormReplyCreator = new FigmaFilesFormReplyCreator();

        return figmaFilesFormReplyCreator.create(projectFiles, teamId);
    }

    @PostMapping("{teamId}/projects/file")
    public String sendProjectFile(@RequestBody InputPayload request, @PathVariable String teamId) {
        System.out.println(request);
        String fileKey = request.getValues().getFile().getValue();
        request.getValues().setTeamId(teamId);
        log.info("Get files: " + request.getValues().getFile().getValue() + " has come");
        log.debug("Get files request: " + request);

        Set<FileInfo> filesByChannelId = subscribeService.getFilesByChannelId(request);
        Optional<FileInfo> file = filesByChannelId.stream().filter(f -> fileKey.equals(f.getFileId())).findAny();

        if (file.isPresent()) {
            return String.format("{\"text\":\"This channel is already subscribed to updates about %s\"}", file.get().getFileName());
        }

        fileNotificationService.subscribeToFileNotification(request);
        userDataKVService.saveUserData(request);
        subscribeService.subscribeToFile(request);
        return "{\"text\":\"Subscribed\"}";
    }

    @PostMapping("/subscriptions")
    public String sendChannelSubscriptions(@RequestBody InputPayload request) throws IOException {
        System.out.println(request);
        log.info("Get Subscriptions for channel: " + request.getContext().getChannel().getId() + " has come");
        log.debug("Get files request: " + request);

        if (!subscribeService.isBotExistsInChannel(request)) {
            throw new MMSubscriptionInChannelWithoutBotException();
        }

        subscribeService.sendSubscriptionFilesToMMChannel(request);
        return "{\"type\":\"ok\"}";
    }

    @PostMapping("/project-files/file/{fileId}/remove")
    public String unsubscribe(@PathVariable String fileId, @RequestBody InputPayload request) {
        subscribeService.unsubscribeFromFile(request, fileId);
        return "{\"text\":\"Unsubscribed\"}";
    }

    @PostMapping("/project/{projectId}/remove")
    public String unsubscribeFromProject(@PathVariable String projectId, @RequestBody InputPayload request) {
        subscribeService.unsubscribeFromProject(request, projectId);
        return "{\"text\":\"Unsubscribed\"}";
    }
}
