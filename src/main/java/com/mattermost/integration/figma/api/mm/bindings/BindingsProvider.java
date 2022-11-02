package com.mattermost.integration.figma.api.mm.bindings;

import com.mattermost.integration.figma.input.mm.binding.*;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.mattermost.integration.figma.input.mm.binding.Command.*;

@Component
public class BindingsProvider {
    private static final String TYPE_OK = "ok";
    private static final String COMMAND_LOCATION = "/command";
    private static final String FIGMA = "figma";
    private static final String ICON = "icon.png";
    private static final String ALL = "all";
    private static final String FIELD_TEXT_TYPE = "text";

    @Autowired
    private MessageSource messageSource;

    public BindingsDTO createDefaultBindingsWithoutCommands() {
        BindingsDTO bindingsDTO = new BindingsDTO();
        bindingsDTO.setData(Collections.singletonList(createSingleData()));
        bindingsDTO.setType(TYPE_OK);
        return bindingsDTO;
    }

    private Datum createSingleData() {
        Datum datum = new Datum();
        datum.setLocation(COMMAND_LOCATION);
        datum.setBindings(Collections.singletonList(createSingleBindings()));
        return datum;
    }

    private Bindings createSingleBindings() {
        Bindings bindings = new Bindings();
        bindings.setAppId(FIGMA);
        bindings.setDescription("Figma commands");
        bindings.setIcon(ICON);
        bindings.setLabel(FIGMA);
        bindings.setLocation(FIGMA);
        bindings.setBindings(new ArrayList<>());
        return bindings;
    }

    public Binding createConfigureBinding(Locale locale) {
        return createBaseCommand(CONFIGURE.getTitle(), createConfigureForm(locale), null);
    }

    public Binding createConnectBinding() {
        return createBaseCommand(CONNECT.getTitle(), null, createConnectSubmit());
    }

    public Binding createDisconnectBinding() {
        return createBaseCommand(DISCONNECT.getTitle(), null, createDisconnectSubmit());
    }

    public Binding createSubscribeBinding(Locale locale) {
        return createBaseCommand(SUBSCRIBE.getTitle(), createSubscribeForm(locale), null);
    }

    public Binding createListBinding() {
        return createBaseCommand(LIST.getTitle(), null, createListSubmit());
    }

    private Submit createListSubmit() {
        Submit listSubmit = new Submit();
        listSubmit.setPath("/subscriptions");
        Expand expand = new Expand();
        expand.setApp(ALL);
        expand.setOauth2App(ALL);
        expand.setChannel(ALL);
        expand.setOauth2User(ALL);
        expand.setActingUser(ALL);
        expand.setActingUserAccessToken(ALL);
        listSubmit.setExpand(expand);
        return listSubmit;
    }

    private Form createSubscribeForm(Locale locale) {
        Form subscribeForm = new Form();
        String title = messageSource.getMessage("mm.form.subscribe.title", null, locale);
        subscribeForm.setTitle(title);
        subscribeForm.setIcon(ICON);
        subscribeForm.setSubmit(createSubscribeSubmit());
        return subscribeForm;
    }

    private Submit createSubscribeSubmit() {
        Submit subscribeSubmit = new Submit();
        subscribeSubmit.setPath("/".concat(SUBSCRIBE.getTitle()));
        Expand expand = new Expand();
        expand.setActingUserAccessToken(ALL);
        expand.setApp(ALL);
        expand.setActingUser(ALL);
        expand.setOauth2User(ALL);
        expand.setChannel(ALL);
        expand.setOauth2App(ALL);
        subscribeSubmit.setExpand(expand);
        return subscribeSubmit;
    }

    private Submit createDisconnectSubmit() {
        Submit connectSubmit = createConnectSubmit();
        connectSubmit.setPath("/".concat(DISCONNECT.getTitle()));
        return connectSubmit;
    }

    private Submit createConnectSubmit() {
        Submit submit = new Submit();
        submit.setPath("/".concat(CONNECT.getTitle()));
        Expand expand = new Expand();
        expand.setApp(ALL);
        expand.setOauth2App(ALL);
        expand.setOauth2User(ALL);
        expand.setActingUser(ALL);
        expand.setActingUserAccessToken(ALL);
        submit.setExpand(expand);
        return submit;
    }

    private Form createConfigureForm(Locale locale) {
        Form configureForm = new Form();
        configureForm.setIcon(ICON);
        String title = messageSource.getMessage("mm.form.configure.title", null, locale);
        configureForm.setTitle(title);

        Submit submit = new Submit();
        submit.setPath("/".concat(CONFIGURE.getTitle()));

        Expand expand = new Expand();
        expand.setActingUser(ALL);
        expand.setActingUserAccessToken(ALL);

        List<Field> fields = Arrays.asList(createSingleField("client_id"), createSingleField("client_secret"));

        submit.setExpand(expand);
        configureForm.setSubmit(submit);
        configureForm.setFields(fields);
        return configureForm;
    }

    private Field createSingleField(String name) {
        Field field = new Field();
        field.setIsRequired(true);
        field.setName(name);
        field.setType(FIELD_TEXT_TYPE);
        field.setLabel(name.replace('_', '-'));
        return field;
    }

    private Binding createBaseCommand(String name, Form form, Submit submit) {
        Binding binding = new Binding();
        binding.setAppId(FIGMA);
        binding.setLabel(name);
        binding.setLocation(name);
        binding.setForm(form);
        binding.setSubmit(submit);
        return binding;
    }
}
