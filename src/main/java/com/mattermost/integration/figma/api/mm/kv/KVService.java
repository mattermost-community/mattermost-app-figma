package com.mattermost.integration.figma.api.mm.kv;

import java.util.Set;

public interface KVService {

    void put(String key, Object value, String mattermostSiteUrl, String token);

    String get(String key, String mattermostSiteUrl, String token);

    void delete(String key, String mattermostSiteUrl, String token);

    void addNewValueToSetInKv(String key, String value, String mmSiteUrl, String botAccessToken, String keyPrefix);

    void deleteValueFromSetInKv(String key, String value, String mattermostSiteUrl, String token, String keyPrefix);

    Set<String> getSetFromKv(String key, String mattermostSiteUrl, String token, String keyPrefix);

    void addValuesToDoubleEndedKvPair(String keyOne, String keyTwo, String keyPrefixOne, String keyPrefixTwo, String mattermostSiteUrl, String token);

    void deleteValuesFromDoubleEndedKvPair(String keyOne, String keyTwo, String keyPrefixOne, String keyPrefixTwo, String mattermostSiteUrl, String token);
}
