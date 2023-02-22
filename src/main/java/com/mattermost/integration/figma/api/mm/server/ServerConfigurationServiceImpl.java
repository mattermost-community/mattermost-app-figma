package com.mattermost.integration.figma.api.mm.server;

import com.mattermost.integration.figma.api.figma.file.dto.FigmaProjectFileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Service
public class ServerConfigurationServiceImpl implements ServerConfigurationService{
    private static final String GET_CONFIGURATION = "%s/api/v4/config/client?format=old";

    @Autowired
    @Qualifier("mmRestTemplate")
    private RestTemplate restTemplate;


    @Override
    public Locale getServerLocale(String mattermostSiteUrl, String botAccessToken) {
        String url = String.format(GET_CONFIGURATION, mattermostSiteUrl);
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", botAccessToken));
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<MMServerClientConfig> resp = restTemplate.exchange(url, HttpMethod.GET, request, MMServerClientConfig.class);
        return Locale.forLanguageTag(resp.getBody().getDefaultClientLocale());
    }
}
