package com.mattermost.integration.figma.config.exception.handler;

import com.mattermost.integration.figma.config.exception.exceptions.figma.*;
import com.mattermost.integration.figma.config.exception.exceptions.mm.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {FigmaResourceNotFoundException.class})
    @ResponseBody
    public String handleFigmaResourceNotFoundException(
            RuntimeException ex, WebRequest request) {
        return "{\"type\":\"error\",\"text\":\"Figma resource was not found\"}";
    }

    @ExceptionHandler(value = {FigmaBadRequestException.class})
    @ResponseBody
    public String handleFigmaBadRequestException(RuntimeException ex, WebRequest request) {

        String requestUrl = ((ServletWebRequest) request).getRequest().getRequestURI();
        String[] path = requestUrl.split("/");
        String url = path[path.length - 1];
        String message = ex.getMessage();
        if ("projects".equals(url) || "file".equals(url)) {
            message = new FigmaBasicTeamSubscriptionException().getMessage();
        }
        return String.format("{\"type\":\"error\",\"text\":\"%s\"}", message);
    }

    @ExceptionHandler(value = {FigmaResourceForbiddenException.class})
    @ResponseBody
    public String handleFigmaResourceForbiddenException(
            RuntimeException ex, WebRequest request) {
        return "{\"type\":\"error\",\"text\":\"Figma resource was forbidden\"}";
    }

    @ExceptionHandler(value = {
            MMSubscriptionFromDMChannelException.class,
            MMSubscriptionInChannelWithoutBotException.class,
            MMSubscriptionToFileInSubscribedProjectException.class,
            MMFigmaUserNotSavedException.class,
            MMFigmaCredsNotSavedException.class,
            MMBadRequestException.class,
            MMForbiddenException.class,
            FigmaBasicTeamSubscriptionException.class,
            FigmaNoFilesInProjectSubscriptionException.class,
            FigmaNoProjectsInTeamSubscriptionException.class,
            FigmaReplyErrorException.class,
            FigmaCannotCreateWebhookException.class
    })
    @ResponseBody
    public String handleExceptions(
            RuntimeException ex, WebRequest request) {
        return String.format("{\"type\":\"error\",\"text\":\"%s\"}", ex.getMessage());
    }
}
