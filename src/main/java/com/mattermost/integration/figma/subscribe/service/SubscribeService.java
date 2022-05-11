package com.mattermost.integration.figma.subscribe.service;

import com.mattermost.integration.figma.input.oauth.InputPayload;

public interface SubscribeService {
    void subscribe(InputPayload payload);
    void sendSubscriptionFilesToMMChannel(InputPayload payload);
    void unsubscribe(InputPayload payload, String fileKey);


}
