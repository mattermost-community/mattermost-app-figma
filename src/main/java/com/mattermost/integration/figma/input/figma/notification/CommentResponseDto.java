package com.mattermost.integration.figma.input.figma.notification;

import lombok.Data;

import java.util.List;

@Data
public class CommentResponseDto {
    private List<CommentDto> comments;
}
