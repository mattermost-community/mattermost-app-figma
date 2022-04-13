package com.mattermost.integration.figma.reply;

import com.mattermost.integration.figma.api.figma.comment.service.CommentService;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReplyController {

    @Autowired
    private CommentService postCommentService;
    @Autowired
    private OAuthService oAuthService;



    @PostMapping("/reply")
    public String reply(@RequestBody InputPayload payload) {

        String commentId = payload.getValues().getCommentId();
        String fileId = payload.getValues().getFileId();
        String message = payload.getValues().getMessage();

        String clientId = payload.getContext().getOauth2().getClientId();
        String clientSecret = payload.getContext().getOauth2().getClientSecret();

        String refreshToken = payload.getContext().getOauth2().getUser().getRefreshToken();
        FigmaOAuthRefreshTokenResponseDTO token = oAuthService.refreshToken(clientId, clientSecret, refreshToken);
        postCommentService.postComment(fileId , commentId , message , token.getAccessToken());

        return "{\"type\":\"ok\"}";
    }
}
