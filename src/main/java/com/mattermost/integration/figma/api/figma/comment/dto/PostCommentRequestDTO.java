package com.mattermost.integration.figma.api.figma.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentRequestDTO {
    @JsonProperty("message")
    private String message;
    @JsonProperty("comment_id")
    private String commentId;
}
