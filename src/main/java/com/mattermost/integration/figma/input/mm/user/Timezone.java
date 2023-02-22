package com.mattermost.integration.figma.input.mm.user;

import lombok.Data;

@Data
public class Timezone {
    public boolean useAutomaticTimezone;
    public String manualTimezone;
    public String automaticTimezone;
}
