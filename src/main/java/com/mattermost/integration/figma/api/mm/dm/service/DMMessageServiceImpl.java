package com.mattermost.integration.figma.api.mm.dm.service;

import com.mattermost.integration.figma.api.mm.dm.dto.*;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DMMessageServiceImpl implements DMMessageService {

    private static final String CREATE_CHANNEL_URL = "/api/v4/channels/direct";
    private static final String SEND_DM_URL = "/api/v4/posts";


    @Autowired
    private RestTemplate restTemplate;


    @Override
    public String createDMChannel(InputPayload payload) {
        String botAccessToken = payload.getContext().getBotAccessToken();
        String botUserId = payload.getContext().getBotUserId();
        String userId = payload.getContext().getActingUser().getId();
        String mattermostSiteUrl = payload.getContext().getMattermostSiteUrl();
        return createDMChannel(new DMChannelPayload(userId, botUserId, botAccessToken, mattermostSiteUrl));
    }

    @Override
    public String createDMChannel(DMChannelPayload payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("Bearer %s", payload.getToken()));
        String[] requestBody = {payload.getUserId(), payload.getBotId()};
        HttpEntity<String[]> request = new HttpEntity(requestBody, headers);
        ResponseEntity<DirectChannelResponseDTO> resp = restTemplate.postForEntity(String.format("%s%s", payload.getMmSiteUrlBase(), CREATE_CHANNEL_URL), request, DirectChannelResponseDTO.class);
        return resp.getBody().getId();
    }

    @Override
    public void sendDMMessage(DMMessagePayload payload) {
        DirectMessageDTO dm = new DirectMessageDTO(payload.getChannelId(), payload.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("Bearer %s", payload.getToken()));

        HttpEntity<String[]> request = new HttpEntity(dm, headers);

        restTemplate.postForEntity(String.format("%s%s", payload.getMmSiteUrlBase(), SEND_DM_URL), request, DirectMessageResponseDTO.class);
    }

    @Override
    public void sendDMMessage(DMMessageWithPropsPayload payload) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("Bearer %s", payload.getToken()));

        HttpEntity<String[]> request = new HttpEntity(payload.getBody(), headers);

        restTemplate.postForEntity(String.format("%s%s", payload.getMmSiteUrl(), SEND_DM_URL), request, DirectMessageResponseDTO.class);
    }


    @Override
    public String getMessageWithReplyButton(DMMessageWithPropsFields payload) {
        String s = "{\"channel_id\":\"%s\"," +
                "\"props\":" +
                "{\"app_bindings\":" +
                "[{" +
                "\"app_id\":\"%s\"," +
                "\"label\":\"%s\"," +
                "\"description\":\"%s\"," +
                "\"bindings\":" +
                "[{\"" +
                "location\":\"my_button\"," +
                "\"label\":\"Reply\"," +
                "\"form\":" +
                "{\"title\":\"Replytoconnent\"," +
                "\"icon\":\"icon.png\"," +
                "\"submit\":{\"path\":\"/reply\"," +
                "\"expand\":" +
                "{\"acting_user_access_token\":\"all\"," +
                "\"app\":\"all\",\"oauth2_app\":\"all\"," +
                "\"oauth2_user\":\"all\"" +
                        "}}," +
                "\"fields\":" +
                "[{\"name\":\"comment_id\"," +
                "\"type\":\"text\"," +
                "\"value\":\"%s\"," +
                "\"is_required\":true," +
                "\"label\":\"comment_id\"}," +
                "{\"name\":\"file_id\"," +
                "\"type\":\"text\"," +
                "\"value\":\"%s\"," +
                "\"is_required\":true," +
                "\"label\":\"file_id\"}," +
                "{\"name\":\"message\"," +
                "\"type\":\"text\"," +
                "\"is_required\":true," +
                "\"label\":\"message\"" +
                "}]}}]}]}}";
        return String.format(s,payload.getChannelId(),payload.getAppId(),payload.getLabel(), payload.getDescription(),payload.getReplyCommentId(),payload.getReplyFileId());
    }

}
