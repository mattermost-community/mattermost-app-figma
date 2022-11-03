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

import java.util.Locale;
import java.util.Objects;

@Service
@Slf4j
public class BindingServiceImpl implements BindingService {
    private static final String ADMIN_ROLE = "system_admin";
    private static final String TEAM_ADMIN = "team_admin";
    private static final String CHANNEL_ADMIN = "channel_admin";
    private static final int FIRST_INSTANCE = 0;

    @Autowired
    private BindingsProvider bindingsProvider;

    public BindingsDTO filterBindingsDependingOnUser(InputPayload payload) {
        Locale locale = Locale.forLanguageTag(payload.getContext().getActingUser().getLocale());
        BindingsDTO defaultBindings = bindingsProvider.createDefaultBindingsWithoutCommands();
        String userRoles = payload.getContext().getActingUser().getRoles();
        User user = payload.getContext().getOauth2().getUser();

        if (userRoles.contains(ADMIN_ROLE)) {
            addCommandToBindings(defaultBindings, bindingsProvider.createConfigureBinding(locale));
        }

        if (Objects.isNull(user) || Objects.isNull(user.getUserId())) {
            addCommandToBindings(defaultBindings, bindingsProvider.createConnectBinding());
            return defaultBindings;
        }

        addCommandToBindings(defaultBindings, bindingsProvider.createDisconnectBinding());

        if ("D".equalsIgnoreCase(payload.getContext().getChannel().getType())) {
            return defaultBindings;
        }


        if ((userRoles.contains(TEAM_ADMIN) || userRoles.contains(CHANNEL_ADMIN) || userRoles.contains(ADMIN_ROLE))) {
            addCommandToBindings(defaultBindings, bindingsProvider.createSubscribeBinding(locale));
        }

        addCommandToBindings(defaultBindings, bindingsProvider.createListBinding());


        return defaultBindings;
    }

    private void addCommandToBindings(BindingsDTO bindings, Binding binding) {
        bindings.getData().get(FIRST_INSTANCE).getBindings().get(FIRST_INSTANCE).getBindings().add(binding);
    }
}
