package com.mattermost.integration.figma.security;

import com.mattermost.integration.figma.config.exception.exceptions.mm.MMFigmaCredsNotSavedException;
import com.mattermost.integration.figma.input.oauth.Context;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.input.oauth.OAuth2;
import com.mattermost.integration.figma.security.dto.FigmaTokenDTO;
import com.mattermost.integration.figma.security.service.OAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private Context context;
    @Mock
    private OAuth2 oAuth2;
    @Mock
    private FigmaTokenDTO figmaTokenDTO;

    @BeforeEach
    void setUp() {
        when(inputPayload.getContext()).thenReturn(context);
        when(context.getOauth2()).thenReturn(oAuth2);
    }

    @Test
    void shouldThrowMMFigmaCredsNotSavedExceptionWhenFigmaCredentialsNotStored() {
        assertThrows(MMFigmaCredsNotSavedException.class, () -> testedInstance.getOauthForm(inputPayload));
    }

    @Test
    void shouldReturnURL() {
        when(oAuth2.getCompleteUrl()).thenReturn(URL);
        when(oAuth2.getClientId()).thenReturn(CLIENT_ID);
        when(oAuthService.generateUrl(any())).thenReturn(URL);

        String url = testedInstance.getOauthForm(inputPayload);

        assertEquals(EXPECTED_RESPONSE, url);
    }

    @Test
    void shouldStoreCreds() {
        testedInstance.posOauthCreds(inputPayload);

        verify(oAuthService).storeOAuthCreds(inputPayload);
    }

    @Test
    void shouldReturnConnectUrl() {
        when(oAuthService.getConnectUrl(inputPayload)).thenReturn(URL);

        String connectUrl = testedInstance.connect(inputPayload);

        assertEquals(EXPECTED_CONNECT_URL, connectUrl);
    }

    @Test
    void shouldStoreFigmaUserToken() {
        when(oAuthService.getFigmaUserToken(inputPayload)).thenReturn(figmaTokenDTO);

        testedInstance.postOauthClientSecret(inputPayload);

        verify(oAuthService).storeFigmaUserToken(inputPayload, figmaTokenDTO);
    }
}