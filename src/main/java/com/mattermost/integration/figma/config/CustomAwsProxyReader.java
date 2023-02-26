package com.mattermost.integration.figma.config;

import com.amazonaws.serverless.exceptions.InvalidRequestEventException;
import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletRequestReader;
import com.amazonaws.serverless.proxy.model.ApiGatewayRequestIdentity;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyRequestContext;
import com.amazonaws.serverless.proxy.model.ContainerConfig;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;
import java.lang.reflect.Field;

@AllArgsConstructor
public class CustomAwsProxyReader extends AwsProxyHttpServletRequestReader {

    @Override
    public HttpServletRequest readRequest(AwsProxyRequest request, SecurityContext securityContext, Context lambdaContext, ContainerConfig config) throws InvalidRequestEventException {

        LambdaLogger logger = lambdaContext.getLogger();
        ObjectMapper objectMapper = new ObjectMapper();


        if (request.getRequestContext() == null) {
            try {
                AwsProxyRequestContext awsProxyRequestContext = new AwsProxyRequestContext();
                ApiGatewayRequestIdentity apiGatewayRequestIdentity = new ApiGatewayRequestIdentity();
                setEmpty(apiGatewayRequestIdentity);
                setEmpty(awsProxyRequestContext);
                awsProxyRequestContext.setIdentity(apiGatewayRequestIdentity);
                request.setRequestContext(awsProxyRequestContext);
            } catch (IllegalAccessException e) {
                logger.log(e.getMessage());
            }
        }

        try {
            String s = objectMapper.writeValueAsString(request);
            logger.log("AwsProxyRequest=" + s);

        } catch (JsonProcessingException e) {
            logger.log(e.getMessage());
        }


        return super.readRequest(request, securityContext, lambdaContext, config);
    }

    public static void setEmpty(Object object) throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (String.class.equals(field.getType())) {
                field.setAccessible(true);
                if (field.get(object) == null) {
                    field.set(object, "");
                }
            }
        }
    }
}
