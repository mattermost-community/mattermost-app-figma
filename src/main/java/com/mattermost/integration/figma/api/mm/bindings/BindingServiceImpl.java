package com.mattermost.integration.figma.api.mm.bindings;

import com.mattermost.integration.figma.api.mm.user.MMUserService;
import com.mattermost.integration.figma.input.mm.binding.Binding;
import com.mattermost.integration.figma.input.mm.binding.BindingsDTO;
import com.mattermost.integration.figma.input.mm.user.MMUser;
import com.mattermost.integration.figma.input.oauth.InputPayload;
import com.mattermost.integration.figma.input.oauth.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class BindingServiceImpl implements BindingService {
    private static final String ADMIN_ROLE = "system_admin";
    private static final String TEAM_ADMIN = "team_admin";
    private static final String CHANNEL_ADMIN = "channel_admin";
    private static final int FIRST_INSTANCE = 0;

    @Autowired
    private MMUserService mmUserService;

    @Autowired
    private BindingsProvider bindingsProvider;

    public BindingsDTO filterBindingsDependingOnUser(InputPayload payload) {
        BindingsDTO defaultBindings = bindingsProvider.createDefaultBindingsWithoutCommands();

        String mmSiteUrl = payload.getContext().getMattermostSiteUrl();
        String botAccessToken = payload.getContext().getBotAccessToken();

        MMUser currentUser = mmUserService.getUserById(payload.getContext().getActingUser().getId(), mmSiteUrl, botAccessToken);
        String userRoles = currentUser.getRoles();

        User user = payload.getContext().getOauth2().getUser();

        if (userRoles.contains(ADMIN_ROLE)) {
            addCommandToBindings(defaultBindings, bindingsProvider.createConfigureBinding());
        }

        if (Objects.isNull(user)) {
            addCommandToBindings(defaultBindings, bindingsProvider.createConnectBinding());
            return defaultBindings;
        }

        if (Objects.nonNull(user.getUserId()) &&
                (userRoles.contains(TEAM_ADMIN) || userRoles.contains(CHANNEL_ADMIN) || userRoles.contains(ADMIN_ROLE))) {
            addCommandToBindings(defaultBindings, bindingsProvider.createSubscribeBinding());
        }

        if (Objects.isNull(user.getUserId())) {
            addCommandToBindings(defaultBindings, bindingsProvider.createConnectBinding());
        }

        else {
            addCommandToBindings(defaultBindings, bindingsProvider.createListBinding());
            addCommandToBindings(defaultBindings, bindingsProvider.createDisconnectBinding());
        }

        return defaultBindings;
    }

    private void addCommandToBindings(BindingsDTO bindings, Binding binding) {
        bindings.getData().get(FIRST_INSTANCE).getBindings().get(FIRST_INSTANCE).getBindings().add(binding);
    }
}
