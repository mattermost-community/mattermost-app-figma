package com.mattermost.integration.figma.api.mm.kv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class KVServiceImpl implements KVService {
    private static final String KV_URL = "/plugins/com.mattermost.apps/api/v1/kv/";

    @Autowired
    private RestTemplate restTemplate;


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
}
