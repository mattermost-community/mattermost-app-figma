package com.mattermost.integration.figma.config.exception.handler;

import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaResourceForbiddenException;
import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaResourceNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @ExceptionHandler(value = {FigmaResourceForbiddenException.class})
    @ResponseBody
    public String handleFigmaResourceForbiddenException(
            RuntimeException ex, WebRequest request) {
        return "{\"type\":\"error\",\"text\":\"Figma resource was forbidden\"}";
    }
}
