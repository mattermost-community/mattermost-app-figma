package com.mattermost.integration.figma.api.mm.bindings;

import com.mattermost.integration.figma.input.mm.binding.BindingsDTO;
import com.mattermost.integration.figma.input.oauth.InputPayload;

public interface BindingService {
    BindingsDTO filterBindingsDependingOnUser(InputPayload payload);
}
