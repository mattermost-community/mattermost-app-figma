package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.figma.team.dto.TeamNameDto;
import com.mattermost.integration.figma.input.mm.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.mattermost.integration.figma.api.mm.dm.component.ExpandCreator.prepareExpand;

@Component
public class TeamNameFormCreator {
    private final static String CREATE_NEW_WEBHOOK = "new_webhook";
    private static final String FIELD_TYPE = "text";
    private static int FIRST_FIELD = 0;

    @Autowired
    private MessageSource messageSource;

    public FormType createTeamSubscribeForm(List<TeamNameDto> teamNameDtos, Locale locale) {
        FormType.FormTypeBuilder builder = FormType.builder();
        builder.type("form");
        builder.form(createForm(teamNameDtos, locale));
        return builder.build();
    }

    private Form createForm(List<TeamNameDto> teamNameDtos, Locale locale) {
        String title = messageSource.getMessage("mm.form.subscribe.notifications.title", null, locale);
        Form form = new Form();
        form.setTitle(title);
        form.setFields(createFields(teamNameDtos, locale));
        form.setSubmit(createSubmit());
        form.setSource(createProjectFilesSubmit());
        return form;
    }

    private List<Field> createFields(List<TeamNameDto> teamNameDtos, Locale locale) {
        String label = messageSource.getMessage("mm.form.subscribe.notifications.field.team.name.label", null, locale);

        StaticSelectField.StaticSelectFieldBuilder<?, ?> builder = StaticSelectField.builder();
        builder.options(createOptions(teamNameDtos, locale));
        String webhookLabel = messageSource.getMessage("mm.form.subscribe.notifications.field.new.webhook.label", null, locale);

        builder.objectValue(Value.builder().value(CREATE_NEW_WEBHOOK).label(webhookLabel).build());
        builder.refresh(true);
        builder.isRequired(true);
        builder.name("team_name");
        builder.label(label);
        builder.type("static_select");
        List<Field> fields = new ArrayList<>();
        fields.add(builder.build());
        String teamIdLabel = messageSource.getMessage("mm.form.subscribe.notifications.field.team.id.label", null, locale);
        fields.add(prepareSingleTextField("team_id", "", teamIdLabel));
        return fields;
    }

    private Field prepareSingleTextField(String name, String value, String label) {
        TextField.TextFieldBuilder<?, ?> builder = TextField.builder();
        builder.name(name);
        builder.label(label);
        builder.value(value);
        builder.type(FIELD_TYPE);
        builder.isRequired(true);
        return builder.build();
    }

    private Submit createProjectFilesSubmit() {
        Submit submit = createSubmit();
        submit.setExpand(prepareExpand());
        submit.setPath("/team/refresh");
        return submit;
    }

    private Submit createSubmit() {
        Submit submit = new Submit();
        submit.setPath("/team/subscribe");
        submit.setExpand(prepareExpand());
        return submit;
    }

    private List<Option> createOptions(List<TeamNameDto> teamNameDtos, Locale locale) {
        String withId = messageSource.getMessage("mm.form.subscribe.notifications.with.id", null, locale);
        List<Option> options = teamNameDtos.stream().map(teamNameDto ->
                        Option.builder().label(String.format("%s %s %s", teamNameDto.getTeamName(), withId, teamNameDto.getTeamId())
                        ).value(teamNameDto.getTeamId()).build())
                .collect(Collectors.toList());
        String label = messageSource.getMessage("mm.form.subscribe.notifications.field.new.webhook.label", null, locale);
        options.add(0, Option.builder().label(label).value(CREATE_NEW_WEBHOOK).build());
        return options;
    }

    public void modifyFormFirstField(FormType form, String teamLabel, String teamValue) {
        Value.ValueBuilder valueBuilder = Value.builder();
        valueBuilder.value(teamValue);
        valueBuilder.label(teamLabel);
        StaticSelectField projectField = (StaticSelectField) form.getForm().getFields().get(FIRST_FIELD);
        projectField.setObjectValue(valueBuilder.build());
        form.getForm().getFields().set(FIRST_FIELD, projectField);
    }
}

