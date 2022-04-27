package com.mattermost.integration.figma.api.mm.kv;

import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.security.dto.UserDataDto;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

@Service
public class UserDataKVServiceImpl implements UserDataKVService {
    private final KVService kvService;
    private final JsonUtils jsonUtils;

    public UserDataKVServiceImpl(KVService kvService, JsonUtils jsonUtils) {
        this.kvService = kvService;
        this.jsonUtils = jsonUtils;
    }

    public UserDataDto getUserData(String userId, String mmSiteUrl, String botAccessToken) {
        return (UserDataDto) jsonUtils.convertStringToObject(kvService.get(userId, mmSiteUrl,
                botAccessToken), UserDataDto.class).get();
    }

    @Override
    public void saveUserData(InputPayload inputPayload) {
        String userId = inputPayload.getContext().getOauth2().getUser().getUserId();
        String mmSiteUrl = inputPayload.getContext().getMattermostSiteUrl();
        String botAccessToken = inputPayload.getContext().getBotAccessToken();
        String teamId = inputPayload.getValues().getTeamId();

        UserDataDto currentData = getUserData(userId, mmSiteUrl, botAccessToken);
        if (Objects.nonNull(currentData.getTeamIds()) && !currentData.getTeamIds().isEmpty()) {
            currentData.getTeamIds().add(teamId);
            updateUserData(inputPayload, currentData);
        } else {
            UserDataDto newUserData = new UserDataDto();
            newUserData.setTeamIds(new HashSet<>(Collections.singletonList(teamId)));
            updateUserData(inputPayload, newUserData);
        }
    }

    private void updateUserData(InputPayload inputPayload, UserDataDto currentData) {
        String userId = inputPayload.getContext().getOauth2().getUser().getUserId();
        String mmSiteUrl = inputPayload.getContext().getMattermostSiteUrl();
        String botAccessToken = inputPayload.getContext().getBotAccessToken();

        currentData.setClientId(inputPayload.getContext().getOauth2().getClientId());
        currentData.setClientSecret(inputPayload.getContext().getOauth2().getClientSecret());
        currentData.setRefreshToken(inputPayload.getContext().getOauth2().getUser().getRefreshToken());
        currentData.setMmUserId(inputPayload.getContext().getActingUser().getId());
        kvService.put(userId, currentData, mmSiteUrl, botAccessToken);
    }
}
