package com.mattermost.integration.figma.api.mm.kv;

import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.UserDataDto;

import java.util.Set;

public interface UserDataKVService {
    UserDataDto getUserData(String userId, String mmSiteUrl, String botAccessToken);

    void saveUserData(InputPayload inputPayload);

    Set<String> getUserIdsByTeamId(String teamId, String mmSiteUrl, String botAccessToken);
}
