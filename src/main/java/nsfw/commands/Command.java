package nsfw.commands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandBuilder;
import nsfw.post.api.PostFetchException;

public interface Command {

    SlashCommandBuilder toSlashCommandBuilder();

    void apply(SlashCommandCreateEvent event) throws CommandException, PostFetchException;
}
