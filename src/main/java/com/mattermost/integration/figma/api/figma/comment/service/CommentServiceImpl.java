package com.mattermost.integration.figma.api.figma.comment.service;

import com.mattermost.integration.figma.api.figma.comment.dto.PostCommentRequestDTO;
import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaReplyErrorException;
import com.mattermost.integration.figma.input.figma.notification.CommentDto;
import com.mattermost.integration.figma.input.figma.notification.CommentResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private static final String GET_COMMENTS_URL = "https://api.figma.com/v1/files/%s/comments";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<CommentDto> getCommentsFromFile(String fileKey, String figmaToken) {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", figmaToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<CommentResponseDto> resp = restTemplate.exchange(String.format(GET_COMMENTS_URL, fileKey), HttpMethod.GET, request, CommentResponseDto.class);
        return Objects.requireNonNull(resp.getBody()).getComments();
    }

    @Override
    public Optional<CommentDto> getCommentById(String commentId, String fileKey, String figmaToken) {
        return getCommentsFromFile(fileKey, figmaToken).stream().filter(comment -> comment
                .getId().equals(commentId)).findFirst();
    }

    @Override
    public void postComment(String fileId, String replyCommentId, String message, String token) {
        String url = String.format(GET_COMMENTS_URL, fileId);

        PostCommentRequestDTO postCommentRequestDTO = new PostCommentRequestDTO();
        postCommentRequestDTO.setCommentId(replyCommentId);
        postCommentRequestDTO.setMessage(message);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", token));

        HttpEntity request = new HttpEntity(postCommentRequestDTO, headers);
        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (final HttpServerErrorException | HttpClientErrorException e) {
            throw new FigmaReplyErrorException("This comment is unavailable.");
        }
    }
}
