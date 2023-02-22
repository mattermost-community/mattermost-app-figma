package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.mm.dm.dto.DMMessageWithPropsPayload;
import com.mattermost.integration.figma.api.mm.dm.dto.FileSubscriptionMessage;
import com.mattermost.integration.figma.api.mm.dm.dto.ProjectSubscriptionMessage;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.kv.dto.ProjectInfo;
import com.mattermost.integration.figma.api.mm.user.MMUserService;
import com.mattermost.integration.figma.input.mm.binding.Expand;
import com.mattermost.integration.figma.input.mm.form.*;
import com.mattermost.integration.figma.input.mm.user.MMUser;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.mattermost.integration.figma.api.mm.dm.component.ExpandCreator.prepareExpand;

@Component
public class DMCallButtonMessageCreator {
    private static final String LOCATION = "delete_subscription_button";
    private static final String FILE_URL = "https://www.figma.com/file/%s";
    private static final String PROJECT_URL = "https://www.figma.com/files/project/%s";

    @Autowired
    private MMUserService mmUserService;

    @Autowired
    private MessageSource messageSource;

    public DMMessageWithPropsPayload createDMMessageWithPropsPayload(FileSubscriptionMessage fileSubscriptionMessage) {

        return getDmMessageWithPropsPayload(fileSubscriptionMessage.getPayload(), createFormReply(fileSubscriptionMessage));
    }

    public DMMessageWithPropsPayload createDMMessageWithPropsPayload(ProjectSubscriptionMessage projectSubscriptionMessage) {

        return getDmMessageWithPropsPayload(projectSubscriptionMessage.getPayload(), createFormReply(projectSubscriptionMessage));
    }

    private DMMessageWithPropsPayload getDmMessageWithPropsPayload(InputPayload payload2, DMFormMessageReply formReply) {
        String mmSiteUrl = payload2.getContext().getMattermostSiteUrl();
        String botAccessToken = payload2.getContext().getBotAccessToken();
        DMFormMessageReply reply = formReply;
        DMMessageWithPropsPayload payload = new DMMessageWithPropsPayload();
        payload.setBody(reply);
        payload.setMmSiteUrl(mmSiteUrl);
        payload.setToken(botAccessToken);
        return payload;
    }

    private DMFormMessageReply createFormReply(FileSubscriptionMessage fileSubscriptionMessage) {
        return getDmFormMessageReply(fileSubscriptionMessage.getPayload(), prepareAppBindings(fileSubscriptionMessage));
    }

    private DMFormMessageReply createFormReply(ProjectSubscriptionMessage projectSubscriptionMessage) {
        return getDmFormMessageReply(projectSubscriptionMessage.getPayload(), prepareAppBindings(projectSubscriptionMessage));
    }

    private DMFormMessageReply getDmFormMessageReply(InputPayload payload, List<AppBinding> appBindings) {
        DMFormMessageReply reply = new DMFormMessageReply();
        String channelId = payload.getContext().getChannel().getId();
        Props props = new Props();
        props.setAppBindings(appBindings);
        reply.setChannelId(channelId);
        reply.setProps(props);
        return reply;
    }

    private List<AppBinding> prepareAppBindings(FileSubscriptionMessage fileSubscriptionMessage) {
        return Collections.singletonList(prepareSingleAppBinding(fileSubscriptionMessage));
    }

    private List<AppBinding> prepareAppBindings(ProjectSubscriptionMessage projectSubscriptionMessage) {
        return Collections.singletonList(prepareSingleAppBinding(projectSubscriptionMessage));
    }

    private AppBinding prepareSingleAppBinding(FileSubscriptionMessage fileSubscriptionMessage) {
        FileInfo fileInfo = fileSubscriptionMessage.getFileInfo();
        String fileName = fileInfo.getFileName();
        String fileId = fileInfo.getFileId();
        Context context = fileSubscriptionMessage.getPayload().getContext();
        String appId = context.getAppId();
        AppBinding appBinding = new AppBinding();
        appBinding.setAppId(appId);
        String label = String.format("[%s](%s)", fileName, String.format(FILE_URL, fileId));
        appBinding.setLabel(label);

        MMUser user = mmUserService.getUserById(fileInfo.getUserId(), context.getMattermostSiteUrl(), context.getBotAccessToken());
        Locale locale = Locale.forLanguageTag(user.getLocale());
        String description = messageSource.getMessage("mm.subscriptions.file.title", List.of(user.getUsername(), fileInfo.getCreatedAt().toString()).toArray(), locale);
        appBinding.setDescription(description);
        appBinding.setBindings(Collections.singletonList(prepareSingleBinding(fileSubscriptionMessage, locale)));
        return appBinding;
    }

    private AppBinding prepareSingleAppBinding(ProjectSubscriptionMessage projectSubscriptionMessage) {
        ProjectInfo project = projectSubscriptionMessage.getProjectInfo();
        String projectName = project.getName();
        String projectId = project.getProjectId();
        Context context = projectSubscriptionMessage.getPayload().getContext();
        String appId = context.getAppId();
        AppBinding appBinding = new AppBinding();
        appBinding.setAppId(appId);
        String label = String.format("[%s](%s)", projectName, String.format(PROJECT_URL, projectId));
        appBinding.setLabel(label);

        MMUser user = mmUserService.getUserById(project.getUserId(), context.getMattermostSiteUrl(), context.getBotAccessToken());
        Locale locale = Locale.forLanguageTag(user.getLocale());
        String description = messageSource.getMessage("mm.subscriptions.project.title", List.of(user.getUsername(), project.getCreatedAt().toString()).toArray(), locale);
        appBinding.setDescription(description);
        appBinding.setBindings(Collections.singletonList(prepareSingleBinding(projectSubscriptionMessage, locale)));
        return appBinding;
    }

    private Binding prepareSingleBinding(FileSubscriptionMessage fileSubscriptionMessage, Locale locale) {
        String label = messageSource.getMessage("mm.form.subscription.delete.label", null, locale);
        Binding binding = new Binding();
        binding.setLabel(label);
        binding.setLocation(LOCATION);
        binding.setSubmit(prepareSingleCall(fileSubscriptionMessage.getFileInfo().getFileId()));
        return binding;
    }

    private Binding prepareSingleBinding(ProjectSubscriptionMessage projectSubscriptionMessage, Locale locale) {
        String label = messageSource.getMessage("mm.form.subscription.delete.label", null, locale);
        Binding binding = new Binding();
        binding.setLabel(label);
        binding.setLocation(LOCATION);
        binding.setSubmit(prepareSingleCallForProject(projectSubscriptionMessage.getProjectInfo().getProjectId()));
        return binding;
    }

    private Submit prepareSingleCall(String fileId) {
        Submit call = new Submit();
        call.setPath(String.format("/project-files/file/%s/remove", fileId));
        call.setExpand(prepareExpand());
        return call;
    }

    private Submit prepareSingleCallForProject(String projectId) {
        Submit call = new Submit();
        call.setPath(String.format("/project/%s/remove", projectId));
        call.setExpand(prepareExpand());
        return call;
    }
}
