package com.mattermost.integration.figma.subscribe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFilesDTO;
import com.mattermost.integration.figma.api.figma.file.service.FigmaFileService;
import com.mattermost.integration.figma.api.figma.notification.service.FileNotificationService;
import com.mattermost.integration.figma.api.figma.project.dto.ProjectDTO;
import com.mattermost.integration.figma.api.figma.project.dto.TeamProjectDTO;
import com.mattermost.integration.figma.api.figma.project.service.FigmaProjectService;
import com.mattermost.integration.figma.api.mm.kv.UserDataKVService;
import com.mattermost.integration.figma.api.mm.kv.dto.FileInfo;
import com.mattermost.integration.figma.api.mm.user.MMUserService;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMFigmaUserNotSavedException;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMSubscriptionFromDMChannelException;
import com.mattermost.integration.figma.config.exception.exceptions.mm.MMSubscriptionInChannelWithoutBotException;
import com.mattermost.integration.figma.input.mm.form.FormType;
import com.mattermost.integration.figma.input.mm.form.MMStaticSelectField;
import com.mattermost.integration.figma.input.oauth.*;
import com.mattermost.integration.figma.subscribe.service.SubscribeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class SubscribeControllerTest {

    private static final String PUBLIC = "P";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CLIENT_SECRET = "CLIENT_SECRET";
    private static final String TEAM_ID = "TEAM_ID";
    private static final String NAME = "NAME";
    private static final String SUBSCRIBED = "{\"text\":\"This channel is already subscribed to updates about NAME\"}";
    private final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String DM_CHANNEL = "D";
    private static final String VALUE = "value";
    private static final String ALL_FILES = "all_files";

    @InjectMocks
    private SubscribeController testedInstance;

    @Mock
    private FileNotificationService fileNotificationService;
    @Mock
    private SubscribeService subscribeService;
    @Mock
    private UserDataKVService userDataKVService;
    @Mock
    private FigmaProjectService figmaProjectService;
    @Mock
    private FigmaFileService figmaFileService;

    @Mock
    private MMUserService userService;

    @Mock
    private InputPayload inputPayload;
    @Mock
    private Context context;
    @Mock
    private OAuth2 oAuth2;
    @Mock
    private Channel channel;
    @Mock
    private User user;
    @Mock
    private TeamProjectDTO teamProjectDTO;
    @Mock
    private Values values;
    @Mock
    private FigmaProjectFilesDTO figmaProjectFilesDTO;
    @Mock
    private MMStaticSelectField field;
    @Mock
    private MMStaticSelectField file;
    @Mock
    private FileInfo fileInfo;
    @Mock
    private ProjectDTO projectDTO;
    @Mock
    private FigmaProjectFileDTO figmaProjectFileDTO;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ActingUser actingUser;

    private final String payload = "";

    @BeforeEach
    void setUp() throws JsonProcessingException {
        when(mapper.readValue(payload, InputPayload.class)).thenReturn(inputPayload);
        when(inputPayload.getContext()).thenReturn(context);
        when(inputPayload.getValues()).thenReturn(values);
        when(context.getOauth2()).thenReturn(oAuth2);
        when(oAuth2.getUser()).thenReturn(user);

        when(context.getChannel()).thenReturn(channel);

        when(context.getActingUser()).thenReturn(actingUser);
        when(actingUser.getLocale()).thenReturn("en");

    }

    @Test
    public void shouldSubscribe() {
        when(user.getRefreshToken()).thenReturn(REFRESH_TOKEN);
        when(oAuth2.getClientId()).thenReturn(CLIENT_ID);
        when(oAuth2.getClientSecret()).thenReturn(CLIENT_SECRET);
        when(channel.getType()).thenReturn(PUBLIC);
        when(subscribeService.isBotExistsInChannel(inputPayload)).thenReturn(true);
        when(figmaProjectService.getProjectsByTeamId(inputPayload)).thenReturn(teamProjectDTO);
        when(teamProjectDTO.getProjects()).thenReturn(Collections.singletonList(projectDTO));
        when(values.getTeamId()).thenReturn(TEAM_ID);

        FormType subscribe = testedInstance.subscribe(inputPayload);

        assertNotNull(subscribe);
    }

    @Test
    public void shouldThrowMMSubscriptionFromDMChannelExceptionWhenChannelIsDM() {
        when(channel.getType()).thenReturn(DM_CHANNEL);
        when(context.getActingUser()).thenReturn(actingUser);
        when(actingUser.getLocale()).thenReturn("en");
        assertThrows(MMSubscriptionFromDMChannelException.class, () -> testedInstance.subscribe(inputPayload));
    }

    @Test
    public void shouldAddBotWhenBotNotInChannel() {
        when(channel.getType()).thenReturn(PUBLIC);
        when(subscribeService.isBotExistsInChannel(inputPayload)).thenReturn(false);

        when(user.getRefreshToken()).thenReturn(REFRESH_TOKEN);
        when(oAuth2.getClientId()).thenReturn(CLIENT_ID);
        when(oAuth2.getClientSecret()).thenReturn(CLIENT_SECRET);
        when(figmaProjectService.getProjectsByTeamId(inputPayload)).thenReturn(teamProjectDTO);
        when(teamProjectDTO.getProjects()).thenReturn(Collections.singletonList(projectDTO));
        when(values.getTeamId()).thenReturn(TEAM_ID);

        testedInstance.subscribe(inputPayload);

        verify(userService).addUserToChannel(any(), any(), any(), any());
    }

    @Test
    public void shouldThrowMMFigmaUserNotSavedException() {
        when(channel.getType()).thenReturn(PUBLIC);
        when(subscribeService.isBotExistsInChannel(inputPayload)).thenReturn(true);

        assertThrows(MMFigmaUserNotSavedException.class, () -> testedInstance.subscribe(inputPayload));
    }

    @Test
    public void shouldSubscribeToProject() throws JsonProcessingException {
        MMStaticSelectField file = new MMStaticSelectField();
        file.setValue(ALL_FILES);
        when(values.getFile()).thenReturn(file);
        when(values.getProject()).thenReturn(field);
        when(field.getLabel()).thenReturn("test");
        when(field.getValue()).thenReturn("test");
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("You have successfully subscribed %s to %s notifications");

        String actualResponse = (String) testedInstance.sendProjectFiles(payload, TEAM_ID);

        assertEquals("{\"text\":\"You have successfully subscribed null to test notifications\"}", actualResponse);
    }

    @Test
    public void shouldCreateFilesForm() throws JsonProcessingException {
        when(values.getFile()).thenReturn(file);
        when(file.getValue()).thenReturn("test");
        when(figmaFileService.getProjectFiles(inputPayload)).thenReturn(figmaProjectFilesDTO);
        when(values.getProject()).thenReturn(field);
        when(figmaProjectFilesDTO.getFiles()).thenReturn(Collections.singletonList(figmaProjectFileDTO));
        when(figmaProjectService.getProjectsByTeamId(inputPayload)).thenReturn(teamProjectDTO);
        when(teamProjectDTO.getProjects()).thenReturn(Collections.singletonList(projectDTO));
        when(projectDTO.getId()).thenReturn("test");
        when(projectDTO.getName()).thenReturn("test");

        FormType formType = (FormType) testedInstance.sendProjectFileSelection(payload, TEAM_ID);

        assertNotNull(formType);
    }

    @Test
    public void shouldSubscribeToFile() throws JsonProcessingException {
        when(values.getFile()).thenReturn(file);
        when(file.getValue()).thenReturn(VALUE);
        when(values.getProject()).thenReturn(field);
        when(file.getLabel()).thenReturn("test");
        when(messageSource.getMessage(any(), any(), any())).thenReturn("You have successfully subscribed %s to %s notifications");

        String actual = (String) testedInstance.sendProjectFiles(payload, TEAM_ID);

        assertEquals("{\"text\":\"You have successfully subscribed null to test notifications\"}", actual);
    }

    @Test
    public void shouldReturnAlreadySubscribedMessage() throws JsonProcessingException {
        when(values.getFile()).thenReturn(file);
        when(file.getValue()).thenReturn(VALUE);

        when(subscribeService.getFilesByChannelId(inputPayload)).thenReturn(Collections.singleton(fileInfo));
        when(fileInfo.getFileId()).thenReturn(VALUE);
        when(fileInfo.getFileName()).thenReturn(NAME);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("This channel is already subscribed to updates about %s");

        String actual = (String) testedInstance.sendProjectFiles(payload, TEAM_ID);

        assertEquals(SUBSCRIBED, actual);
    }

    @Test
    public void shouldInvokeSendSubscriptionFilesToMMChannel() throws JsonProcessingException {
        when(subscribeService.isBotExistsInTeam(inputPayload)).thenReturn(true);
        when(subscribeService.isBotExistsInChannel(inputPayload)).thenReturn(true);
        when(user.getRefreshToken()).thenReturn(REFRESH_TOKEN);
        when(oAuth2.getClientId()).thenReturn(CLIENT_ID);
        when(oAuth2.getClientSecret()).thenReturn(CLIENT_SECRET);

        testedInstance.sendChannelSubscriptions(payload);

        verify(subscribeService).sendSubscriptionFilesToMMChannel(inputPayload);
    }

    @Test
    public void shouldThrowMMSubscriptionInChannelWithoutBotException() {
        when(subscribeService.isBotExistsInTeam(inputPayload)).thenReturn(true);
        when(subscribeService.isBotExistsInChannel(inputPayload)).thenReturn(false);

        assertThrows(MMSubscriptionInChannelWithoutBotException.class, () -> testedInstance.sendChannelSubscriptions(payload));
    }

    @Test
    public void shouldThrowMMSubscriptionInTeamWithoutBotException() {
        when(subscribeService.isBotExistsInTeam(inputPayload)).thenReturn(false);

        assertThrows(MMSubscriptionInChannelWithoutBotException.class, () -> testedInstance.sendChannelSubscriptions(payload));
    }

    @Test
    public void shouldThrowMMFigmaUserNotSavedExceptionForListOfSubscriptions() {
        when(subscribeService.isBotExistsInTeam(inputPayload)).thenReturn(true);
        when(subscribeService.isBotExistsInChannel(inputPayload)).thenReturn(true);

        assertThrows(MMFigmaUserNotSavedException.class, () -> testedInstance.sendChannelSubscriptions(payload));
    }
}