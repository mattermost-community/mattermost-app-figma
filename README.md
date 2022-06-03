# Figma App

A Figma app for Mattermost.

### First steps 

#### Setting up 
1. Run Mattermost server https://github.com/mattermost/mattermost-server/blob/master/README.md
2. Install/Enable Apps plugin  https://github.com/mattermost/mattermost-plugin-apps
3. Register a figma app - https://www.figma.com/developers/api#register-oauth2
   * as a callback url use - http://MM_INSTANCE__ROOT_URL/plugins/com.mattermost.apps/apps/figma/oauth2/remote/complete
4. Run Figma integration server app (docker-compose up)
5. In mattermost channel run `/apps install http http://localhost:8080/manifest.json`

#### Link MM account with Figma

1. Run command `/figma configure` and provide client id and client secret from figma app https://www.figma.com/developers/apps
2. Run command `/figma connect` and open link from bot response for linking mm with figma account 

### Usage

1. Run command `/figma subscribe` for enabling notifications from figma project or file.
   <br /> `Note`: For this you will need to provide a team id.
   <br />The only way for this it's copy it from Figma url
   https://www.figma.com/files/team/{team_id}/{team_name}
   <br /> Forum topics regarding this: 
   * https://forum.figma.com/t/suggestion-api-endpoint-for-team-id/3429
   * https://spectrum.chat/figma/general/how-to-get-team-id-and-project-id-for-api~191126f9-ec4f-4c4f-9964-766ae2b4916f
2. Run command `/figma list`  for subscriptions in current channel
