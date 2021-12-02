package commands;

import api.Api;
import enums.DiscordServer;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.entity.server.Server;

import java.util.Arrays;
import java.util.Optional;

@Log4j2
public class CommandUtil {

    public static void createForServers(ServerCommand serverCommand) {
        Arrays.stream(DiscordServer.values()).forEach(discordServer -> {
            Optional<Server> optionalServer = Api.getAPI().getServerById(discordServer.getId());

            if (optionalServer.isEmpty()) {
                log.warn("Server with id {} not found", discordServer.getId());
            } else {
                serverCommand.toSlashCommandBuilder().createForServer(optionalServer.get()).join();
                log.info("Created server {}", discordServer.getId());
            }
        });
    }
}
