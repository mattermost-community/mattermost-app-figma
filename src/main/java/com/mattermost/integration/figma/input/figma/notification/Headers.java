package com.mattermost.integration.figma.input.figma.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Headers {
    @JsonProperty("Accept-Encoding")
    private String acceptEncoding;
    @JsonProperty("Content-Length")
    private String contentLength;
    @JsonProperty("Content-Type")
    private String contentType;
    @JsonProperty("Mattermost-Session-Id")
    private String mattermostSessionId;
    @JsonProperty("User-Agent")
    private String userAgent;
    @JsonProperty("X-Datadog-Parent-Id")
    private String xDatadogParentId;
    @JsonProperty("X-Datadog-Sampling-Priority")
    private String xDatadogSamplingPriority;
    @JsonProperty("X-Datadog-Trace-Id")
    private String xDatadogTraceId;
    @JsonProperty("X-Forwarded-For")
    private String xForwardedFor;
    @JsonProperty("X-Forwarded-Proto")
    private String xForwardedProto;
}
