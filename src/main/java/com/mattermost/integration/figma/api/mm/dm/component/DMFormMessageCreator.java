package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.mm.dm.dto.DMMessageWithPropsFields;
import com.mattermost.integration.figma.input.mm.form.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DMFormMessageCreator {
    private static final String LOCATION = "my_button";
    private static final String REPLY = "Reply";
    private static final String ICON = "icon.png";
    private static final String TITLE = "Reply to comment";
    private static final String REPLY_PATH = "/reply";
    private static final String ALL = "all";
    private static final String FIELD_TYPE = "text";
    private static final String COMMENT_ID = "comment_id";
    private static final String FILE_ID = "file_id";
    private static final String MESSAGE = "message";

    public DMFormMessageReply createFormReply(DMMessageWithPropsFields fields) {
        DMFormMessageReply reply = new DMFormMessageReply();
        Props props = new Props();
        props.setAppBindings(prepareAppBindings(fields));
        reply.setChannelId(fields.getChannelId());
        reply.setProps(props);
        return reply;
    }

    private List<AppBinding> prepareAppBindings(DMMessageWithPropsFields fields) {
        return Collections.singletonList(prepareSingleAppBinding(fields));
    }

    private AppBinding prepareSingleAppBinding(DMMessageWithPropsFields fields) {
        AppBinding appBinding = new AppBinding();
        appBinding.setAppId(fields.getAppId());
        appBinding.setLabel(fields.getLabel());
        appBinding.setDescription(fields.getDescription());
        appBinding.setBindings(Collections.singletonList(prepareSingleBinding(fields)));
        return appBinding;
    }

    private Binding prepareSingleBinding(DMMessageWithPropsFields fields) {
        Binding binding = new Binding();
        binding.setLabel(REPLY);
        binding.setLocation(LOCATION);
        binding.setForm(prepareSingleForm(fields));
        return binding;
    }

    private Form prepareSingleForm(DMMessageWithPropsFields fields) {
        Form form = new Form();
        form.setTitle(TITLE);
        form.setIcon(ICON);
        form.setSubmit(prepareSubmit());
        form.setFields(prepareFields(fields));
        return form;
    }

    private Submit prepareSubmit() {
        Submit submit = new Submit();
        submit.setPath(REPLY_PATH);
        submit.setExpand(prepareExpand());
        return submit;
    }

    private Expand prepareExpand() {
        Expand expand = new Expand();
        expand.setActingUserAccessToken(ALL);
        expand.setApp(ALL);
        expand.setOauth2App(ALL);
        expand.setOauth2User(ALL);
        return expand;
    }

    private List<Field> prepareFields(DMMessageWithPropsFields fields) {
        Field commentIdField = prepareSingleField(COMMENT_ID, fields.getReplyCommentId(), COMMENT_ID);
        Field fileIdField = prepareSingleField(FILE_ID, fields.getReplyFileId(), FILE_ID);
        Field message = prepareSingleField(MESSAGE, "", MESSAGE);

        return Arrays.asList(commentIdField, fileIdField, message);
    }

    private Field prepareSingleField(String name, String value, String label) {
        Field field = new Field();
        field.setName(name);
        field.setLabel(label);
        field.setValue(value);
        field.setType(FIELD_TYPE);
        field.setRequired(true);
        return field;
    }
}
