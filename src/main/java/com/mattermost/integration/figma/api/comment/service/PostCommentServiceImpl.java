package com.mattermost.integration.figma.api.comment.service;

import com.mattermost.integration.figma.api.comment.dto.PostCommentRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PostCommentServiceImpl implements PostCommentService {

    private static final String BASE_URL = "https://api.figma.com/v1/files/%s/comments";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void postComment(String fileId, String replyCommentId, String message, String token) {
        String url = String.format(BASE_URL, fileId);

        PostCommentRequestDTO postCommentRequestDTO = new PostCommentRequestDTO();
        postCommentRequestDTO.setCommentId(replyCommentId);
        postCommentRequestDTO.setMessage(message);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", token));

        HttpEntity request = new HttpEntity(postCommentRequestDTO ,headers);
        restTemplate.postForEntity(url, request, String.class);
    }
}
