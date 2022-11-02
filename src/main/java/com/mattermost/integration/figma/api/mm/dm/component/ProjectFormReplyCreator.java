package com.mattermost.integration.figma.api.mm.dm.component;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.input.mm.binding.Expand;
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
public class ProjectFormReplyCreator {

    private MessageSource messageSource;

    public ProjectFormReplyCreator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public FormType create(TeamProjectDTO teamProjectDTO, String teamId, Locale locale) {
        FormType.FormTypeBuilder builder = FormType.builder();
        builder.type("form");
        builder.form(createForm(teamProjectDTO, teamId, locale));
        return builder.build();
    }

    private Form createForm(TeamProjectDTO teamProjectDTO, String teamId, Locale locale) {
        Form.FormBuilder<?, ?> builder = Form.builder();
        builder.fields(createField(teamProjectDTO, locale));
        String title = messageSource.getMessage("mm.form.subscribe.team.projects.title", null, locale);

        builder.title(title);
        builder.submit(createSubmit(teamId));
        builder.source(createProjectFilesSubmit(teamId));
        return builder.build();
    }

    private Submit createProjectFilesSubmit(String teamId) {
        Submit submit = createSubmit(teamId);
        submit.setPath(String.format("/%s/projectFiles", teamId));
        return submit;
    }

    private List<Field> createField(TeamProjectDTO teamProjectDTO, Locale locale) {
        String label = messageSource.getMessage("mm.form.subscribe.team.projects.project.label", null, locale);
        StaticSelectField.StaticSelectFieldBuilder<?, ?> builder = StaticSelectField.builder();
        builder.name("project_id");
        builder.type("static_select");
        builder.isRequired(true);
        builder.label(label);
        builder.refresh(true);
        builder.options(createOptions(teamProjectDTO));
        List<Field> fields = new ArrayList<>();
        fields.add(builder.build());
        return fields;
    }

    private List<Option> createOptions(TeamProjectDTO teamProjectDTO) {
        return teamProjectDTO.getProjects().stream().map(p -> Option.builder().label(p.getName()).value(p.getId()).build()).collect(Collectors.toList());
    }

    private Submit createSubmit(String teamId) {
        String replyPath = String.format("/%s/projects", teamId);
        Submit submit = new Submit();
        submit.setPath(replyPath);
        submit.setExpand(prepareExpand());
        return submit;
    }


    public void addFilesToForm(List<FigmaProjectFileDTO> files, FormType form, String projectName, String projectId, Locale locale) {
        String label = messageSource.getMessage("mm.form.subscribe.team.projects.file.label", null, locale);
        Value.ValueBuilder projectValueBuilder = Value.builder();
        projectValueBuilder.label(projectName);
        projectValueBuilder.value(projectId);
        StaticSelectField projectField = (StaticSelectField) form.getForm().getFields().get(0);
        projectField.setObjectValue(projectValueBuilder.build());
        form.getForm().getFields().set(0, projectField);
        StaticSelectField.StaticSelectFieldBuilder<?, ?> builder = StaticSelectField.builder();
        Value.ValueBuilder fileValueBuilder = Value.builder();
        fileValueBuilder.value("all_files");
        fileValueBuilder.label("All files");
        builder.name("file_id");
        builder.type("static_select");
        builder.isRequired(true);
        builder.label(label);
        builder.options(createFileOptions(files, locale));
        builder.objectValue(fileValueBuilder.build());
        form.getForm().getFields().add(builder.build());
    }

    private List<Option> createFileOptions(List<FigmaProjectFileDTO> files, Locale locale) {
        String label = messageSource.getMessage("mm.form.subscribe.team.project.files.label", null, locale);
        List<Option> fileOptions = files.stream().map(f -> Option.builder().label(f.getName()).value(f.getKey()).build()).collect(Collectors.toList());
        fileOptions.add(0, Option.builder().label(label).value("all_files").build());
        return fileOptions;
    }

}
