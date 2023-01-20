# discord-java-nsfw-bot

A Discord bot that uses Slash Commands to show images from NSFW websites.

Sites supported
- rule34.xxx
- danbooru
- gelbooru
- xbooru
- the big imageboard

Uses a MySQL database.

## How to run
- Create a properties file named `config.properties` in `src/main/resources/` with your bot's token
```properties
token=test
```
- Then create a properties file named `db.properties` in `src/main/resources/` where you define your database connection like this
```properties
url=mysql_url
db=database_name
user=database_user
password=database_password
```

## Known issues
- Autocomplete in these boorus works slightly differently from Discord's slash commands. It is not possible to have autocomplete for multiple results in Discord, therefore you can only use autocomplete for 1 string at a time.
