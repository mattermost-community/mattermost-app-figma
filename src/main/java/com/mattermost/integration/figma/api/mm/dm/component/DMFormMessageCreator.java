package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.mm.dm.dto.DMMessageWithPropsFields;
import com.mattermost.integration.figma.api.mm.dm.dto.DMMessageWithPropsPayload;
import com.mattermost.integration.figma.input.mm.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.mattermost.integration.figma.api.mm.dm.component.ExpandCreator.prepareExpand;

@Component
public class DMFormMessageCreator {
    private static final String LOCATION = "my_button";
    private static final String REPLY = "mm.form.reply.button.label";
    private static final String TITLE = "mm.form.reply.title";
    private static final String REPLY_PATH = "/reply";
    private static final String FIELD_TYPE = "text";
    private static final String MESSAGE = "message";
    private static final String MESSAGE_LABEL = "mm.form.reply.message.label";
    private static final String TEXTAREA = "textarea";

    @Autowired
    private MessageSource messageSource;

    public DMMessageWithPropsPayload createMessageWithPropsPayload(DMMessageWithPropsFields fields, String botAccessToken,
                                                                     String mmSiteUrl, Locale locale) {

        DMFormMessageReply reply = createFormReply(fields , locale);
        DMMessageWithPropsPayload payload = new DMMessageWithPropsPayload();
        payload.setBody(reply);
        payload.setMmSiteUrl(mmSiteUrl);
        payload.setToken(botAccessToken);
        return payload;
    }

    private DMFormMessageReply createFormReply(DMMessageWithPropsFields fields ,Locale locale) {
        DMFormMessageReply reply = new DMFormMessageReply();
        Props props = new Props();
        props.setAppBindings(prepareAppBindings(fields , locale));
        reply.setChannelId(fields.getChannelId());
        reply.setProps(props);
        return reply;
    }

    private List<AppBinding> prepareAppBindings(DMMessageWithPropsFields fields, Locale locale) {
        return Collections.singletonList(prepareSingleAppBinding(fields , locale));
    }

    private AppBinding prepareSingleAppBinding(DMMessageWithPropsFields fields ,Locale locale) {
        AppBinding appBinding = new AppBinding();
        appBinding.setAppId("figma");
        appBinding.setLabel(fields.getLabel());
        appBinding.setDescription(fields.getDescription());
        appBinding.setBindings(Collections.singletonList(prepareSingleBinding(fields, locale)));
        return appBinding;
    }

    private Binding prepareSingleBinding(DMMessageWithPropsFields fields, Locale locale) {
        String reply = messageSource.getMessage(REPLY, null, locale);

        Binding binding = new Binding();
        binding.setLabel(reply);
        binding.setLocation(LOCATION);
        binding.setForm(prepareSingleForm(fields , locale));
        return binding;
    }

    private Form prepareSingleForm(DMMessageWithPropsFields fields, Locale locale) {
        String message = messageSource.getMessage(MESSAGE_LABEL, null, locale);
        String title = messageSource.getMessage(TITLE, null, locale);
        Form form = new Form();
        form.setTitle(title);
        form.setSubmit(prepareSubmit(fields.getReplyFileId(), fields.getReplyCommentId()));
        form.setFields(List.of(prepareSingleTextAreaField(MESSAGE, "", message)));
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
