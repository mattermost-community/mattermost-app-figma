package com.mattermost.integration.figma.api.mm.kv;

import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.UserDataDto;

import java.util.Optional;
import java.util.Set;

public interface UserDataKVService {
    Optional<UserDataDto> getUserData(String userId, String mmSiteUrl, String botAccessToken);

    void saveUserData(InputPayload inputPayload);

    Set<String> getUserIdsByTeamId(String teamId, String mmSiteUrl, String botAccessToken);

    void storePrimaryUserData(InputPayload inputPayload, UserDataDto currentData);

    void saveNewUserToAllUserIdsSet(String userId, String mmSiteUrl, String botAccessToken);

    Set<String> getAllFigmaUserIds(String mmSiteUrl, String botAccessToken);

    Set<String> getAllFigmaTeamIds(String mmSiteUrl, String botAccessToken);

    void saveUserToCertainTeam(String teamId, String userId, String mmSiteUrl, String botAccessToken);

    void saveNewTeamToAllTeamIdsSet(String teamId, String mmSiteUrl, String botAccessToken);

    void changeUserConnectionStatus(String mmUserId, boolean isConnected, String mmSiteUrl, String botAccessToken);

    String getFigmaUserIdByMMUserId(String mmUserId, String mmSiteUrl, String botAccessToken);
}
