package com.mattermost.integration.figma.api.figma.file.service;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.input.oauth.InputPayload;

import java.util.Optional;

public interface FigmaFileService {

    FigmaProjectFilesDTO getProjectFiles(InputPayload inputPayload);

    Optional<FigmaProjectFilesDTO> getProjectFiles(String projectId, String figmaUserId, String mmUserId, String botAccessToken);

    Optional<FigmaProjectFileDTO> getFileByKey(String fileKey, String accessToken);
}
