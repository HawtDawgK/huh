package nsfw.commands;

import nsfw.post.api.PostFetchException;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandBuilder;

public interface Command {

    SlashCommandBuilder toSlashCommandBuilder();

    void apply(SlashCommandCreateEvent event) throws CommandException, PostFetchException;
}
