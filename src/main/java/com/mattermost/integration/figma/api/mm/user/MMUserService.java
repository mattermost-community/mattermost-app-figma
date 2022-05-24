package com.mattermost.integration.figma.api.mm.user;

import com.mattermost.integration.figma.input.mm.user.MMChannelUser;
import com.mattermost.integration.figma.input.mm.user.MMUser;

import java.util.List;

public interface MMUserService {
    List<MMUser> getUsersById(List<String> ids, String mattermostSiteUrl, String token);
    MMUser getUserById(String id, String mattermostSiteUrl, String token);
    List<MMChannelUser> getUsersByChannelId(String channelId, String mattermostSiteUrl, String token);
}
