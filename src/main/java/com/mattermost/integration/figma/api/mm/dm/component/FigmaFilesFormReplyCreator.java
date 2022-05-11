package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.input.mm.form.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FigmaFilesFormReplyCreator {
    private static final String ALL = "all";


    public FormType create(FigmaProjectFilesDTO projectFilesDTO) {
        FormType.FormTypeBuilder builder = FormType.builder();
        builder.type("form");
        builder.form(createForm(projectFilesDTO));
        return builder.build();
    }

    private Form createForm(FigmaProjectFilesDTO projectFilesDTO) {
        Form.FormBuilder<?, ?> builder = Form.builder();
        builder.fields(createField(projectFilesDTO));
        builder.title("Figma project files");
        builder.submit(createSubmit());
        return builder.build();
    }

    private List<Field> createField(FigmaProjectFilesDTO projectFilesDTO) {
        StaticSelectField.StaticSelectFieldBuilder<?, ?> builder = StaticSelectField.builder();
        builder.name("file_id");
        builder.type("static_select");
        builder.isRequired(true);
        builder.label("File");
        builder.options(createOptions(projectFilesDTO));
        return Collections.singletonList(builder.build());
    }

    private List<Option> createOptions(FigmaProjectFilesDTO projectFilesDTO) {
        return projectFilesDTO.getFiles().stream().map(f -> Option.builder().label(f.getName()).value(f.getKey()).build()).collect(Collectors.toList());
    }

    private Submit createSubmit() {
        String replyPath = "/project-files/file";
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
