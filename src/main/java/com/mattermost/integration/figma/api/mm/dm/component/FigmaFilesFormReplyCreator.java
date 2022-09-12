package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.input.mm.binding.Expand;
import com.mattermost.integration.figma.input.mm.form.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mattermost.integration.figma.api.mm.dm.component.ExpandCreator.prepareExpand;

public class FigmaFilesFormReplyCreator {
    private static final String ALL = "all";


    public FormType create(FigmaProjectFilesDTO projectFilesDTO, String teamId) {
        FormType.FormTypeBuilder builder = FormType.builder();
        builder.type("form");
        builder.form(createForm(projectFilesDTO, teamId));
        return builder.build();
    }

    private Form createForm(FigmaProjectFilesDTO projectFilesDTO, String teamId) {
        Form.FormBuilder<?, ?> builder = Form.builder();
        builder.fields(createField(projectFilesDTO));
        builder.title("Figma project files");
        builder.submit(createSubmit(teamId));
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

    private Submit createSubmit(String teamId) {
        String replyPath = String.format("/%s/projects/file", teamId);
        Submit submit = new Submit();
        submit.setPath(replyPath);
        submit.setExpand(prepareExpand());
        return submit;
    }


}
