package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.figma.team.dto.TeamNameDto;
import com.mattermost.integration.figma.input.mm.form.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mattermost.integration.figma.api.mm.dm.component.ExpandCreator.prepareExpand;

@Component
public class TeamNameFormCreator {
    private final static String CREATE_NEW_WEBHOOK = "new_webhook";
    private static final String FIELD_TYPE = "text";
    private static int FIRST_FIELD = 0;

    public FormType createTeamSubscribeForm(List<TeamNameDto> teamNameDtos) {
        FormType.FormTypeBuilder builder = FormType.builder();
        builder.type("form");
        builder.form(createForm(teamNameDtos));
        return builder.build();
    }

    private Form createForm(List<TeamNameDto> teamNameDtos) {
        Form form = new Form();
        form.setTitle("Subscribe to Figma Notifications");
        form.setFields(createFields(teamNameDtos));
        form.setSubmit(createSubmit());
        form.setSource(createProjectFilesSubmit());
        return form;
    }

    private List<Field> createFields(List<TeamNameDto> teamNameDtos) {
        StaticSelectField.StaticSelectFieldBuilder<?, ?> builder = StaticSelectField.builder();
        builder.options(createOptions(teamNameDtos));
        builder.objectValue(Value.builder().value(CREATE_NEW_WEBHOOK).label("Create new team webhook").build());
        builder.refresh(true);
        builder.isRequired(true);
        builder.name("team_name");
        builder.label("Team name");
        builder.type("static_select");
        List<Field> fields = new ArrayList<>();
        fields.add(builder.build());
        fields.add(prepareSingleTextField("team_id", "", "Team id"));
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

    private List<Option> createOptions(List<TeamNameDto> teamNameDtos) {
        List<Option> options = teamNameDtos.stream().map(teamNameDto ->
                Option.builder().label(teamNameDto.getTeamName().concat(" with id ".concat(teamNameDto.getTeamId())))
                        .value(teamNameDto.getTeamId()).build())
                .collect(Collectors.toList());
        options.add(0, Option.builder().label("Create new team webhook").value(CREATE_NEW_WEBHOOK).build());
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

