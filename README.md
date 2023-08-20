# MasterLink
### a simple plugin for link your server discord with your server minecraft

The plugin uses JDA version 5.0.0-beta.11, and it based on paper 1.16.5 but works fine on 1.19 or under like 1.12.2



### How to set up?

- Go to the [discord developper portal](https://discord.com/developers/applications) and create a new application
- Go to the bot section and create a new bot
- Active the "SERVER MEMBERS INTENT" in the Privileged Gateway Intents section
- Copy the token of your bot and paste it in the config.yml file
- Copy your guild and paste it in the config.yml file
- Copy your log channel and paste it in the config.yml file
- If you want to activate the liked ban
- Launch your server !


### How it works?

The plugin uses a simple system :
- When a player joins the server, he can link his account with the server, he needs to run the command /link <'pseudo'> on discord for get a code liked with his pseudo
- In game, the player runs the command /link <'code'> for links his account with the server
- In discord, the player can retrieve a player with command /retrieve <'pseudo'>, works only if the player has been linked
- In game, the player can run the command /unlink to discard his link with the server
- On discord, if you ban a player who is linked in game, and if you have the option activated, he will be automatically banned



