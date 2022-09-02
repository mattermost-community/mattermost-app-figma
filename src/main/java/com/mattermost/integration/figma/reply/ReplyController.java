package com.mattermost.integration.figma.reply;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattermost.integration.figma.api.figma.comment.service.CommentService;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ReplyController {

    @Autowired
    private CommentService postCommentService;
    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private ObjectMapper mapper;


    @PostMapping("/reply")
    public String reply(@RequestBody String payloadString) throws JsonProcessingException {

        log.debug(payloadString);

        InputPayload payload = mapper.readValue(payloadString, InputPayload.class);

        String fileId = payload.getState().getFileId();
        String commentId = payload.getState().getCommentId();
        String message = payload.getValues().getMessage();

        String clientId = payload.getContext().getOauth2().getClientId();
        String clientSecret = payload.getContext().getOauth2().getClientSecret();

        String refreshToken = payload.getContext().getOauth2().getUser().getRefreshToken();
        FigmaOAuthRefreshTokenResponseDTO token = oAuthService.refreshToken(clientId, clientSecret, refreshToken);
        postCommentService.postComment(fileId, commentId, message, token.getAccessToken());

        return "{\"type\":\"ok\"}";
    }
}
