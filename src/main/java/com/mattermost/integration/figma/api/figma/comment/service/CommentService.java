package com.mattermost.integration.figma.api.figma.comment.service;

import com.mattermost.integration.figma.input.figma.notification.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<CommentDto> getCommentsFromFile(String fileKey, String figmaToken);
    Optional<CommentDto> getCommentById(String commentId, String fileKey, String figmaToken);
}
