package com.mattermost.integration.figma.api.mm.kv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

@Service
public class KVServiceImpl implements KVService {
    private static final String KV_URL = "/plugins/com.mattermost.apps/api/v1/kv/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JsonUtils jsonUtils;


    @Override
    public void put(String key, Object value, String mattermostSiteUrl, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("Bearer %s", token));
        HttpEntity<Object> request = new HttpEntity(value, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(String.format("%s%s%s", mattermostSiteUrl, KV_URL, key), request, String.class);
        resp.getBody();
    }

    @Override
    public String get(String key, String mattermostSiteUrl, String token) {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", token));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(String.format("%s%s%s", mattermostSiteUrl, KV_URL, key), HttpMethod.GET, request, String.class);

        if ("{}".equals(resp.getBody())) {
            return "";
        }

        return resp.getBody();
    }

    @Override
    public void delete(String key, String mattermostSiteUrl, String token) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("Bearer %s", token));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        restTemplate.exchange(String.format("%s%s%s", mattermostSiteUrl, KV_URL, key), HttpMethod.DELETE, request, String.class);

    }

    @Override
    public void addNewValueToSetInKv(String key, String value, String mmSiteUrl, String botAccessToken, String keyPrefix) {
        String valuesString = get(String.format("%s%s", keyPrefix, key), mmSiteUrl, botAccessToken);
        Set<String> values = (Set<String>) jsonUtils.convertStringToObject(valuesString, new TypeReference<Set<String>>() {
        }).orElse(new HashSet<String>());

        values.add(value);
        put(String.format("%s%s", keyPrefix, key), values, mmSiteUrl, botAccessToken);
    }

    @Override
    public void deleteValueFromSetInKv(String key, String value, String mattermostSiteUrl, String token, String keyPrefix) {
        String mmChanelSubscribedFiles =get(String.format("%s%s", keyPrefix, key), mattermostSiteUrl, token);

        if (mmChanelSubscribedFiles.isBlank()) {
            return;
        }
        Set<String> files = (Set<String>) jsonUtils.convertStringToObject(mmChanelSubscribedFiles, new TypeReference<Set<String>>() {
        }).get();
        files.removeIf(s -> s.equals(value));
        put(String.format("%s%s", keyPrefix, key), files, mattermostSiteUrl, token);
    }

    @Override
    public Set<String> getSetFromKv(String key, String mattermostSiteUrl, String token, String keyPrefix) {
        String mmSubscribedChannels = get(String.format("%s%s", keyPrefix, key), mattermostSiteUrl, token);

        if (mmSubscribedChannels.isBlank()) {
            return new HashSet<String>();
        }

        return (Set<String>) jsonUtils.convertStringToObject(mmSubscribedChannels, new TypeReference<Set<String>>() {
        }).orElse(new HashSet<String>());
    }

    @Override
    public void addValuesToDoubleEndedKvPair(String keyOne, String keyTwo, String keyPrefixOne, String keyPrefixTwo, String mattermostSiteUrl, String token) {
        addNewValueToSetInKv(keyOne, keyTwo, mattermostSiteUrl, token, keyPrefixOne);
        addNewValueToSetInKv(keyTwo, keyOne, mattermostSiteUrl, token, keyPrefixTwo);
    }

    @Override
    public void deleteValuesFromDoubleEndedKvPair(String keyOne, String keyTwo, String keyPrefixOne, String keyPrefixTwo, String mattermostSiteUrl, String token) {
        deleteValueFromSetInKv(keyOne, keyTwo, mattermostSiteUrl, token, keyPrefixOne);
        deleteValueFromSetInKv(keyTwo, keyOne, mattermostSiteUrl, token, keyPrefixTwo);
    }
}
