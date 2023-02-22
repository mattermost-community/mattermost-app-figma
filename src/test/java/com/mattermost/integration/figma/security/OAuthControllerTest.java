package com.mattermost.integration.figma.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMFigmaCredsNotSavedException;
import com.mattermost.integration.figma.input.oauth.ActingUser;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.input.oauth.OAuth2;
import com.mattermost.integration.figma.security.dto.FigmaTokenDTO;
import com.mattermost.integration.figma.security.service.OAuthService;
import jdk.jfr.MemoryAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OAuthControllerTest {

    public static final String URL = "URL";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String EXPECTED_RESPONSE = "{\"type\":\"ok\",\"data\":\"URL\"}";
    public static final String EXPECTED_CONNECT_URL = "{\"type\":\"ok\",\"text\":\"[Connect](URL) to Figma.\"}";
    @InjectMocks
    private OAuthController testedInstance;

    @Mock
    private OAuthService oAuthService;

    @Mock
    private InputPayload inputPayload;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Context context;
    @Mock
    private OAuth2 oAuth2;
    @Mock
    private FigmaTokenDTO figmaTokenDTO;
    @Mock
    private UserDataKVService userDataKVService;
    @Mock
    private ActingUser actingUser;
    @Mock
    private MessageSource messageSource;

    private final String payloadString = "";


    @BeforeEach
    void setUp() throws JsonProcessingException {
        when(objectMapper.readValue(payloadString, InputPayload.class)).thenReturn(inputPayload);
        when(inputPayload.getContext()).thenReturn(context);
        when(context.getOauth2()).thenReturn(oAuth2);
        when(context.getActingUser()).thenReturn(actingUser);
        when(actingUser.getLocale()).thenReturn("en");

    }

    @Test
    void shouldThrowMMFigmaCredsNotSavedExceptionWhenFigmaCredentialsNotStored() {
        assertThrows(MMFigmaCredsNotSavedException.class, () -> testedInstance.getOauthForm(payloadString));
    }

    @Test
    void shouldReturnURL() {
        when(oAuth2.getCompleteUrl()).thenReturn(URL);
        when(oAuth2.getClientId()).thenReturn(CLIENT_ID);
        when(oAuthService.generateUrl(any())).thenReturn(URL);

        String url = null;
        try {
            url = testedInstance.getOauthForm(payloadString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assertEquals(EXPECTED_RESPONSE, url);
    }

    @Test
    void shouldStoreCreds() throws JsonProcessingException {
        testedInstance.posOauthCreds(payloadString);

        verify(oAuthService).storeOAuthCreds(inputPayload);
    }

    @Test
    void shouldReturnConnectUrl() throws JsonProcessingException {
        when(oAuthService.getConnectUrl(inputPayload)).thenReturn(URL);
        when(messageSource.getMessage(any(),any(),any())).thenReturn("[Connect](%s) to Figma.");

        String connectUrl = testedInstance.connect(payloadString);

        assertEquals(EXPECTED_CONNECT_URL, connectUrl);
    }

    @Test
    void shouldStoreFigmaUserToken() throws JsonProcessingException {
        String testString = "1";
        ActingUser actingUser = new ActingUser();
        actingUser.setId(testString);
        actingUser.setLocale("en");

        when(context.getActingUser()).thenReturn(actingUser);
        when(context.getMattermostSiteUrl()).thenReturn(testString);
        when(context.getBotAccessToken()).thenReturn(testString);
        when(oAuthService.getFigmaUserToken(inputPayload)).thenReturn(figmaTokenDTO);
        doNothing().when(userDataKVService).changeUserConnectionStatus(testString, true, testString, testString);

        testedInstance.postOauthClientSecret(payloadString);

        verify(oAuthService).storeFigmaUserToken(inputPayload, figmaTokenDTO);
        verify(userDataKVService).changeUserConnectionStatus(testString, true, testString, testString);
    }
}