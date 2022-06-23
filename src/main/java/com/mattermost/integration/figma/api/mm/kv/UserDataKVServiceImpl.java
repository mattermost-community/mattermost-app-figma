package com.mattermost.integration.figma.api.mm.kv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.UserDataDto;
import com.mattermost.integration.figma.security.service.DataEncryptionService;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.mattermost.integration.figma.constant.prefixes.global.GlobalPrefixes.*;
import static com.mattermost.integration.figma.constant.prefixes.user.UserPrefixes.*;

@Service
@Slf4j
public class UserDataKVServiceImpl implements UserDataKVService {
    private final KVService kvService;
    private final JsonUtils jsonUtils;
    private final DataEncryptionService dataEncryptionService;

    public UserDataKVServiceImpl(KVService kvService, JsonUtils jsonUtils, DataEncryptionService dataEncryptionService) {
        this.kvService = kvService;
        this.jsonUtils = jsonUtils;
        this.dataEncryptionService = dataEncryptionService;
    }

    @Override
    public Optional<UserDataDto> getUserData(String userId, String mmSiteUrl, String botAccessToken) {
        String userKVString = kvService.get(USER_KV_PREFIX.concat(userId), mmSiteUrl,
                botAccessToken);

        if (StringUtils.isBlank(userKVString)) {
            return Optional.empty();
        }

        UserDataDto currentUserData = (UserDataDto) jsonUtils.convertStringToObject(userKVString, UserDataDto.class).get();
        if (Objects.isNull(currentUserData.getClientSecret()) || Objects.isNull(currentUserData.getRefreshToken())) {
            return Optional.of(currentUserData);
        }
        decryptSensitiveUserData(currentUserData);
        return Optional.of(currentUserData);
    }

    @Override
    public Set<String> getUserIdsByTeamId(String teamId, String mmSiteUrl, String botAccessToken) {
        return getSetFromKV(FIGMA_TEAM_ID_PREFIX.concat(teamId), mmSiteUrl, botAccessToken);
    }

    @Override
    public Set<String> getAllFigmaUserIds(String mmSiteUrl, String botAccessToken) {
        return getSetFromKV(ALL_USERS, mmSiteUrl, botAccessToken);
    }

    @Override
    public Set<String> getAllFigmaTeamIds(String mmSiteUrl, String botAccessToken) {
        return getSetFromKV(ALL_TEAMS, mmSiteUrl, botAccessToken);
    }

    @Override
    public void saveNewUserToAllUserIdsSet(String userId, String mmSiteUrl, String botAccessToken) {
        Set<String> allFigmaUserIds = getAllFigmaUserIds(mmSiteUrl, botAccessToken);
        if (Objects.isNull(allFigmaUserIds)) {
            allFigmaUserIds = new HashSet<>();
        }
        allFigmaUserIds.add(userId);
        kvService.put(ALL_USERS, allFigmaUserIds, mmSiteUrl, botAccessToken);
    }

    @Override
    public void saveNewTeamToAllTeamIdsSet(String teamId, String mmSiteUrl, String botAccessToken) {
        Set<String> allFigmaTeamIds = getAllFigmaUserIds(mmSiteUrl, botAccessToken);
        if (Objects.isNull(allFigmaTeamIds)) {
            allFigmaTeamIds = new HashSet<>();
        }
        allFigmaTeamIds.add(teamId);
        kvService.put(ALL_TEAMS, allFigmaTeamIds, mmSiteUrl, botAccessToken);
    }

    @Override
    public void saveUserToCertainTeam(String teamId, String userId, String mmSiteUrl, String botAccessToken) {
        Set<String> userIds = getUserIdsByTeamId(teamId, mmSiteUrl, botAccessToken);

        if (Objects.isNull(userIds)) {
            userIds = new HashSet<>();
        }
        userIds.add(userId);
        kvService.put(FIGMA_TEAM_ID_PREFIX.concat(teamId), userIds, mmSiteUrl, botAccessToken);
    }


    @Override
    public void saveUserData(InputPayload inputPayload) {
        String userId = inputPayload.getContext().getOauth2().getUser().getUserId();
        String mmSiteUrl = inputPayload.getContext().getMattermostSiteUrl();
        String botAccessToken = inputPayload.getContext().getBotAccessToken();
        String teamId = inputPayload.getValues().getTeamId();

        Optional<UserDataDto> currentData = getUserData(userId, mmSiteUrl, botAccessToken);
        if (currentData.isPresent() && Objects.nonNull(currentData.get().getTeamIds()) && !currentData.get().getTeamIds().isEmpty()) {
            currentData.get().getTeamIds().add(teamId);
            storePrimaryUserData(inputPayload, currentData.get());
        } else {
            UserDataDto newUserData = new UserDataDto();
            newUserData.setTeamIds(new HashSet<>(Collections.singletonList(teamId)));
            storePrimaryUserData(inputPayload, newUserData);
        }
    }

    @Override
    public void storePrimaryUserData(InputPayload inputPayload, UserDataDto currentData) {
        String userId = inputPayload.getContext().getOauth2().getUser().getUserId();
        String mmSiteUrl = inputPayload.getContext().getMattermostSiteUrl();
        String botAccessToken = inputPayload.getContext().getBotAccessToken();

        currentData.setClientId(inputPayload.getContext().getOauth2().getClientId());

        try {
            currentData.setClientSecret(dataEncryptionService.encrypt(inputPayload.getContext().getOauth2().getClientSecret()));
            currentData.setRefreshToken(dataEncryptionService.encrypt(inputPayload.getContext().getOauth2().getUser().getRefreshToken()));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error(e.getMessage());
        }
        currentData.setMmUserId(inputPayload.getContext().getActingUser().getId());
        currentData.setConnected(true);

        kvService.put(USER_KV_PREFIX.concat(userId), currentData, mmSiteUrl, botAccessToken);
        kvService.put(MM_USER_ID_PREFIX.concat(inputPayload.getContext().getActingUser().getId()), userId, mmSiteUrl, botAccessToken);
    }

    @Override
    public void changeUserConnectionStatus(String mmUserId, boolean isConnected, String mmSiteUrl, String botAccessToken) {
        String figmaUserId = getFigmaUserIdByMMUserId(mmUserId, mmSiteUrl, botAccessToken);
        Optional<UserDataDto> userData = getUserData(figmaUserId, mmSiteUrl, botAccessToken);
        userData.ifPresent(currentData ->
                changeUserStatusEncryptDataAndSave(currentData, figmaUserId, isConnected, mmSiteUrl, botAccessToken));
    }

    private void changeUserStatusEncryptDataAndSave(UserDataDto userDataDto, String figmaUserId, boolean isConnected, String mmSiteUrl, String botAccessToken) {
        encryptSensitiveUserData(userDataDto);
        userDataDto.setConnected(isConnected);
        kvService.put(USER_KV_PREFIX.concat(figmaUserId), userDataDto, mmSiteUrl, botAccessToken);
    }

    private void encryptSensitiveUserData(UserDataDto userDataDto) {
        try {
            userDataDto.setClientSecret(dataEncryptionService.encrypt(userDataDto.getClientSecret()));
            userDataDto.setRefreshToken(dataEncryptionService.encrypt(userDataDto.getRefreshToken()));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error(e.getMessage());
        }
    }

    private void decryptSensitiveUserData(UserDataDto userDataDto) {
        try {
            userDataDto.setClientSecret(dataEncryptionService.decrypt(userDataDto.getClientSecret()));
            userDataDto.setRefreshToken(dataEncryptionService.decrypt(userDataDto.getRefreshToken()));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getFigmaUserIdByMMUserId(String mmUserId, String mmSiteUrl, String botAccessToken) {
        return kvService.get(MM_USER_ID_PREFIX.concat(mmUserId), mmSiteUrl, botAccessToken);
    }

    private Set<String> getSetFromKV(String key, String mmSiteUrl, String botAccessToken) {
        return (Set<String>) jsonUtils.convertStringToObject(kvService.get(key, mmSiteUrl,
                botAccessToken), new TypeReference<Set<String>>() {
        }).orElse(null);
    }
}
