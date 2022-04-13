package com.mattermost.integration.figma.api.comment.service;

public interface PostCommentService {

    public void postComment (String fileId , String replyCommentId , String message ,String token);
}
