package com.mattermost.integration.figma.constant.prefixes.user;

public class UserPrefixes {
    // Figma user id -> mmUserId, clientSecret, clientId, figmaRefreshToken
    public final static String USER_KV_PREFIX = "figma-user-id-";
    // Figma team id -> list of users
    public final static String FIGMA_TEAM_ID_PREFIX = "figma-team-id-";
    // mmUserId -> figma user id
    public final static String MM_USER_ID_PREFIX = "mm-user-id-";
}
