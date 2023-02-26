package com.mattermost.integration.figma.api.figma.team;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mattermost.integration.figma.api.figma.team.dto.TeamNameDto;
import com.mattermost.integration.figma.api.mm.kv.KVService;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.mattermost.integration.figma.constant.prefixes.global.GlobalPrefixes.ALL_TEAMS;
import static com.mattermost.integration.figma.constant.prefixes.user.UserPrefixes.MM_USER_ID_PREFIX;
import static com.mattermost.integration.figma.constant.prefixes.webhook.TeamWebhookPrefixes.TEAM_WEBHOOK_PREFIX;

@Service
public class TeamNameServiceImpl implements TeamNameService {
    @Autowired
    private KVService kvService;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private TeamNameParallelSearchService teamNameParallelSearchService;

    public List<TeamNameDto> getAllTeamNames(String mmUserId, String mmSiteUrl, String botAccessToken) {
        Set<String> allTeamIds = getAllDistinctTeamIds(mmSiteUrl, botAccessToken);
        List<TeamNameDto> teamNameDtos = createTeamNameDtos(allTeamIds, mmSiteUrl, botAccessToken);
        String userId = kvService.get(MM_USER_ID_PREFIX.concat(mmUserId), mmSiteUrl, botAccessToken);
        return teamNameParallelSearchService.doTeamNameSearchTask(teamNameDtos, userId, mmSiteUrl, botAccessToken);
    }

    private Set<String> getAllDistinctTeamIds(String mmSiteUrl, String botAccessToken) {
        String ids = kvService.get(ALL_TEAMS, mmSiteUrl, botAccessToken);
        if (StringUtils.isBlank(ids)) {
            return new HashSet<>();
        }
        return (Set<String>) jsonUtils.convertStringToObject(ids, new TypeReference<Set<String>>() {
        }).orElse(new HashSet<String>());
    }

    private List<TeamNameDto> createTeamNameDtos(Set<String> teamIds, String mmSiteUrl, String botAccessToken) {
        return teamIds.stream().map(teamId -> {
            String webhookId = kvService.get(TEAM_WEBHOOK_PREFIX.concat(teamId), mmSiteUrl, botAccessToken);
            TeamNameDto teamNameDto = new TeamNameDto();
            if (StringUtils.isNotBlank(webhookId)) {
                teamNameDto.setTeamId(teamId);
            }
            return teamNameDto;
        }).filter(teamNameDto -> Objects.nonNull(teamNameDto.getTeamId())).collect(Collectors.toList());
    }
}
