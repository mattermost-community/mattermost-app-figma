package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.input.mm.form.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectFormReplyCreator {
    private static final String ALL = "all";


    public FormType create(TeamProjectDTO teamProjectDTO) {
        FormType.FormTypeBuilder builder = FormType.builder();
        builder.type("form");
        builder.form(createForm(teamProjectDTO));
        return builder.build();
    }

    private Form createForm(TeamProjectDTO teamProjectDTO) {
        Form.FormBuilder<?, ?> builder = Form.builder();
        builder.fields(createField(teamProjectDTO));
        builder.title("Figma team projects");
        builder.submit(createSubmit());
        return builder.build();
    }

    private List<Field> createField(TeamProjectDTO teamProjectDTO) {
        StaticSelectField.StaticSelectFieldBuilder<?, ?> builder = StaticSelectField.builder();
        builder.name("project_id");
        builder.type("static_select");
        builder.isRequired(true);
        builder.label("Project");
        builder.options(createOptions(teamProjectDTO));
        Field.FieldBuilder<?, ?> field = Field.builder();
        field.name("is_project_subscription");
        field.type("bool");
        field.isRequired(false);
        field.label("Subscribe to current project");
        return Arrays.asList(builder.build(), field.build());
    }

    private List<Option> createOptions(TeamProjectDTO teamProjectDTO) {
        return teamProjectDTO.getProjects().stream().map(p -> Option.builder().label(p.getName()).value(p.getId()).build()).collect(Collectors.toList());
    }

    private Submit createSubmit() {
        String replyPath = "/project-files";
        Submit submit = new Submit();
        submit.setPath(replyPath);
        submit.setExpand(prepareExpand());
        return submit;
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
