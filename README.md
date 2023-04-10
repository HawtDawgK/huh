# discord-java-nsfw-bot

A Discord bot that uses Slash Commands to show images from NSFW websites.

Sites supported
- rule34.xxx
- danbooru
- gelbooru
- xbooru
- hypnohub
- the big imageboard
- yande.re

Uses a MySQL database.

## How to run
- Create a properties file named `config.properties` in `src/main/resources/` with your bot's token
```properties
token=test
```

An h2 database saved to a file is used, you should not have to do any setup here.

## Known issues
- Autocomplete in these boorus works slightly differently from Discord's slash commands. It is not possible to have autocomplete for multiple results in Discord, therefore you can only use autocomplete for 1 string at a time.
