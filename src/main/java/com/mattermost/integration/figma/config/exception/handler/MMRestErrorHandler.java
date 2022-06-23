package com.mattermost.integration.figma.config.exception.handler;

import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaBadRequestException;
import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaResourceForbiddenException;
import com.mattermost.integration.figma.config.exception.exceptions.figma.FigmaResourceNotFoundException;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMBadRequestException;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMForbiddenException;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMNoAccessTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class MMRestErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (
                response.getStatusCode().series() == CLIENT_ERROR
                        || response.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new MMNoAccessTokenException("no access token provided");

        }

        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new MMBadRequestException("Invalid or missing parameters");
        }

        if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new MMForbiddenException("Do not have appropriate permissions");
        }
    }
}
