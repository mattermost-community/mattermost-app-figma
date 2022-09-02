package com.mattermost.integration.figma.webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattermost.integration.figma.api.figma.notification.service.FileNotificationService;
import com.mattermost.integration.figma.input.figma.notification.FileCommentWebhookResponse;
import com.mattermost.integration.figma.webhook.service.FileCommentService;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
class WebhookControllerTest {

    @InjectMocks
    private WebhookController testedInstance;

    @Mock
    private FileNotificationService fileNotificationService;
    @Mock
    private FileCommentService fileCommentService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FileCommentWebhookResponse response;

    @Mock
    private ObjectMapper mapper;

    private final String payload = "";

    @Test
    public void shouldDoNothingWhenTriggeredWithPingType() throws JsonProcessingException {
        when(response.getValues().getData().getEventType()).thenReturn("PING");
        when(mapper.readValue(payload, FileCommentWebhookResponse.class)).thenReturn(response);

        testedInstance.comment(payload);

        verify(fileCommentService, never()).updateName(response);
        verify(fileNotificationService, never()).sendFileNotificationMessageToMM(response);
        verify(fileNotificationService, never()).sendFileNotificationMessageToMMSubscribedChannels(response);
    }
}