package com.mattermost.integration.figma.api.mm.kv;

public interface KVService {

    void put(String key, Object value, String mattermostSiteUrl, String token);

    String get(String key, String mattermostSiteUrl, String token);

    void delete(String key, String mattermostSiteUrl, String token);
}
