package com.mattermost.integration.figma.api.figma.file.service;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.input.oauth.InputPayload;

public interface FigmaFileService {

    FigmaProjectFilesDTO getProjectFiles(InputPayload inputPayload);

    FigmaProjectFilesDTO getProjectFiles(String projectId, String figmaUserId, String mmUserId, String botAccessToken);

    FigmaProjectFileDTO getFileByKey(String fileKey, String accessToken);
}
