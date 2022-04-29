package com.mattermost.integration.figma.config.exception.handler;

import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaResourceForbiddenException;
import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class FigmaRestErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (
                response.getStatusCode().series() == CLIENT_ERROR
                        || response.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new FigmaResourceNotFoundException();
        }

        if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new FigmaResourceForbiddenException();
        }
    }


}
