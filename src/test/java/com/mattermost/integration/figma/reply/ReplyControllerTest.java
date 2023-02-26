package com.mattermost.integration.figma.reply;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattermost.integration.figma.api.figma.comment.service.CommentService;
import com.mattermost.integration.figma.input.oauth.*;
import com.mattermost.integration.figma.security.dto.FigmaOAuthRefreshTokenResponseDTO;
import com.mattermost.integration.figma.security.service.OAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ReplyControllerTest {

    public static final String FILE_ID = "FILE_ID";
    public static final String COMMENT_ID = "COMMENT_ID";
    public static final String MESSAGE = "MESSAGE";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String CLIENT_SECRET = "CLIENT_SECRET";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    @InjectMocks
    private ReplyController testedInstance;

    @Mock
    private InputPayload inputPayload;
    @Mock
    private CommentService postCommentService;
    @Mock
    private OAuthService oAuthService;
    @Mock
    private OAuth2 oAuth2;
    @Mock
    private Context context;
    @Mock
    private Values values;
    @Mock
    private User user;
    @Mock
    private FigmaOAuthRefreshTokenResponseDTO token;
    @Mock
    private State state;

    @Mock
    private ObjectMapper mapper;

    private final String payload = "";

    @BeforeEach
    void setUp() throws JsonProcessingException {
        when(mapper.readValue(payload, InputPayload.class)).thenReturn(inputPayload);
        when(inputPayload.getContext()).thenReturn(context);
        when(context.getOauth2()).thenReturn(oAuth2);

    }

    @Test
    public void shouldCallPostCommentService() throws JsonProcessingException {
        when(inputPayload.getValues()).thenReturn(values);
        when(inputPayload.getState()).thenReturn(state);
        when(state.getFileId()).thenReturn(FILE_ID);
        when(state.getCommentId()).thenReturn(COMMENT_ID);
        when(values.getMessage()).thenReturn(MESSAGE);
        when(oAuth2.getClientId()).thenReturn(CLIENT_ID);
        when(oAuth2.getClientSecret()).thenReturn(CLIENT_SECRET);
        when(oAuth2.getUser()).thenReturn(user);
        when(user.getRefreshToken()).thenReturn(REFRESH_TOKEN);

        when(oAuthService.refreshToken(CLIENT_ID, CLIENT_SECRET, REFRESH_TOKEN)).thenReturn(token);

        when(token.getAccessToken()).thenReturn(ACCESS_TOKEN);

        testedInstance.reply(payload);

        verify(postCommentService).postComment(inputPayload, ACCESS_TOKEN);
    }
}