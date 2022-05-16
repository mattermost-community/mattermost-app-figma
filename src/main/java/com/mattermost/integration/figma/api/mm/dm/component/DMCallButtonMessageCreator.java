package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.mm.dm.dto.DMMessageWithPropsPayload;
import com.mattermost.integration.figma.api.mm.dm.dto.FileSubscriptionMessage;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.user.MMUserService;
import com.mattermost.integration.figma.input.mm.form.*;
import com.mattermost.integration.figma.input.mm.user.MMUser;
import com.mattermost.integration.figma.input.oauth.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DMCallButtonMessageCreator {
    private static final String LOCATION = "delete_subscription_button";
    private static final String DELETE = "Delete";
    private static final String ALL = "all";
    private static final String FILE_URL = "https://www.figma.com/file/%s";

    @Autowired
    private MMUserService mmUserService;


    public DMMessageWithPropsPayload createDMMessageWithPropsPayload(FileSubscriptionMessage fileSubscriptionMessage) {

        String mmSiteUrl = fileSubscriptionMessage.getPayload().getContext().getMattermostSiteUrl();
        String botAccessToken = fileSubscriptionMessage.getPayload().getContext().getBotAccessToken();
        DMFormMessageReply reply = createFormReply(fileSubscriptionMessage);
        DMMessageWithPropsPayload payload = new DMMessageWithPropsPayload();
        payload.setBody(reply);
        payload.setMmSiteUrl(mmSiteUrl);
        payload.setToken(botAccessToken);
        return payload;
    }

    public DMFormMessageReply createFormReply(FileSubscriptionMessage fileSubscriptionMessage) {
        DMFormMessageReply reply = new DMFormMessageReply();
        String channelId = fileSubscriptionMessage.getPayload().getContext().getChannel().getId();
        Props props = new Props();
        props.setAppBindings(prepareAppBindings(fileSubscriptionMessage));
        reply.setChannelId(channelId);
        reply.setProps(props);
        return reply;
    }

    private List<AppBinding> prepareAppBindings(FileSubscriptionMessage fileSubscriptionMessage) {
        return Collections.singletonList(prepareSingleAppBinding(fileSubscriptionMessage));
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
        String description = String.format("Created by %s on %s", user.getUsername(), fileInfo.getCreatedAt().toString());
        appBinding.setDescription(description);
        appBinding.setBindings(Collections.singletonList(prepareSingleBinding(fileSubscriptionMessage)));
        return appBinding;
    }

    private Binding prepareSingleBinding(FileSubscriptionMessage fileSubscriptionMessage) {
        Binding binding = new Binding();
        binding.setLabel(DELETE);
        binding.setLocation(LOCATION);
        binding.setSubmit(prepareSingleCall(fileSubscriptionMessage.getFileInfo().getFileId()));
        return binding;
    }

    private Submit prepareSingleCall(String fileId) {
        Submit call = new Submit();
        call.setPath(String.format("/project-files/file/%s/remove",fileId));
        call.setExpand(prepareExpand());
        return call;
    }

    private Expand prepareExpand() {
        Expand expand = new Expand();
        expand.setActingUserAccessToken(ALL);
        expand.setApp(ALL);
        expand.setOauth2App(ALL);
        expand.setOauth2User(ALL);
        expand.setChannel(ALL);
        return expand;
    }
}
