package com.mattermost.integration.figma.input.mm;

import lombok.Data;

@Data
public class Timezone {
    public boolean useAutomaticTimezone;
    public String manualTimezone;
    public String automaticTimezone;
}
