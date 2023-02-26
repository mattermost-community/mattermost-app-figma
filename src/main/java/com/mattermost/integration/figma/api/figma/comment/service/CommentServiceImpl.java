package com.mattermost.integration.figma.api.figma.comment.service;

import com.mattermost.integration.figma.api.figma.comment.dto.PostCommentRequestDTO;
import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaReplyErrorException;
import com.mattermost.integration.figma.input.figma.notification.CommentDto;
import com.mattermost.integration.figma.input.figma.notification.CommentResponseDto;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private static final String GET_COMMENTS_URL = "https://api.figma.com/v1/files/%s/comments";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MessageSource messageSource;

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
    public void postComment(InputPayload payload, String token) {

        String fileId = payload.getState().getFileId();
        String replyCommentId = payload.getState().getCommentId();
        String message = payload.getValues().getMessage();
        String url = String.format(GET_COMMENTS_URL, fileId);
        Locale locale = Locale.forLanguageTag(payload.getContext().getActingUser().getLocale());


        PostCommentRequestDTO postCommentRequestDTO = new PostCommentRequestDTO();
        postCommentRequestDTO.setCommentId(replyCommentId);
        postCommentRequestDTO.setMessage(message);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", token));

        HttpEntity request = new HttpEntity(postCommentRequestDTO, headers);
        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (final HttpServerErrorException | HttpClientErrorException e) {
            String errorMessage = messageSource.getMessage("figma.reply.exception", null, locale);
            throw new FigmaReplyErrorException(errorMessage);
        }
    }
}
