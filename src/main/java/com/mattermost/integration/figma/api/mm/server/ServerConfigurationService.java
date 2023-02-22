package com.mattermost.integration.figma.api.mm.server;

import java.util.Locale;

public interface ServerConfigurationService {

    public Locale getServerLocale(String mattermostSiteUrl, String botAccessToken);
}
