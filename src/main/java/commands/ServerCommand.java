package commands;

import api.Api;
import enums.DiscordServer;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandBuilder;

import java.util.Arrays;
import java.util.Optional;

public interface ServerCommand {

    SlashCommandBuilder toSlashCommandBuilder();

    void apply(SlashCommandCreateEvent event) throws CommandException;

    default void createForServers() {
        Arrays.stream(DiscordServer.values()).forEach(ds -> {
            Optional<Server> optionalServer = Api.getAPI().getServerById(ds.getId());

            if (optionalServer.isEmpty()) {
                throw new RuntimeException("Server with id " + ds.getId() + " not found");
            }

            this.toSlashCommandBuilder().createForServer(optionalServer.get()).join();
        });
    }
}
