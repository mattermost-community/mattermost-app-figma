package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.mm.dm.dto.DMMessageWithPropsFields;
import com.mattermost.integration.figma.api.mm.dm.dto.DMMessageWithPropsPayload;
import com.mattermost.integration.figma.input.mm.form.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DMFormMessageCreator {
    private static final String LOCATION = "my_button";
    private static final String REPLY = "Reply";
    private static final String TITLE = "Reply to comment";
    private static final String REPLY_PATH = "/reply";
    private static final String ALL = "all";
    private static final String FIELD_TYPE = "text";
    private static final String MESSAGE = "message";
    private static final String MESSAGE_LABEL = "Message";
    private static final String TEXTAREA = "textarea";

    public DMMessageWithPropsPayload createDMMessageWithPropsPayload(DMMessageWithPropsFields fields, String botAccessToken,
                                                                     String mmSiteUrl) {

        DMFormMessageReply reply = createFormReply(fields);
        DMMessageWithPropsPayload payload = new DMMessageWithPropsPayload();
        payload.setBody(reply);
        payload.setMmSiteUrl(mmSiteUrl);
        payload.setToken(botAccessToken);
        return payload;
    }

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
        form.setSubmit(prepareSubmit(fields.getReplyFileId(), fields.getReplyCommentId()));
        form.setFields(List.of(prepareSingleTextAreaField(MESSAGE, "", MESSAGE_LABEL)));
        return form;
    }

    private Submit prepareSubmit(String fileId, String commentId) {
        Map<String, String> submitState = new HashMap<>();
        submitState.put("fileId", fileId);
        submitState.put("commentId", commentId);
        Submit submit = new Submit();
        submit.setPath(REPLY_PATH);
        submit.setExpand(prepareExpand());
        submit.setState(submitState);
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

    private Field prepareSingleField(String name, String value, String label) {
        TextField.TextFieldBuilder<?, ?> builder = TextField.builder();
        builder.name(name);
        builder.label(label);
        builder.value(value);
        builder.type(FIELD_TYPE);
        builder.isRequired(true);
        return builder.build();
    }

    private TextField prepareSingleTextAreaField(String name, String value, String label) {
        TextField textField = (TextField) prepareSingleField(name, value, label);
        textField.setSubType(TEXTAREA);
        return textField;
    }
}
