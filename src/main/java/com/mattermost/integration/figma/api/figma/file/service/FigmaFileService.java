package com.mattermost.integration.figma.api.figma.file.service;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.input.oauth.InputPayload;

public interface FigmaFileService {

    FigmaProjectFilesDTO getProjectFiles(InputPayload inputPayload);
}
