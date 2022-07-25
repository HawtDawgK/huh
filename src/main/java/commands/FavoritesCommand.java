package commands;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import post.PostMessageFactory;

public class FavoritesCommand implements Command {

    @Override
    public SlashCommandBuilder toSlashCommandBuilder() {
        return SlashCommand.with("favorites", "Show a user's favorites")
                .addOption(new SlashCommandOptionBuilder()
                        .setName("user")
                        .setDescription("User to search favorites you, will be you if unfilled")
                        .setType(SlashCommandOptionType.USER)
                        .build());
    }

    @Override
    public void apply(SlashCommandCreateEvent event) throws CommandException {
        CommandUtil.checkNsfwChannel(event.getInteraction());

        User user = event.getSlashCommandInteraction().getOptionUserValueByName("user")
                .orElse(event.getInteraction().getUser());

        PostMessageFactory.createListPostFromFavorites(event, user);
    }
}
