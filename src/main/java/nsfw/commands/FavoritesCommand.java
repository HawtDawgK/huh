package nsfw.commands;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import nsfw.post.PostMessageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FavoritesCommand implements Command {

    @Autowired
    private PostMessageFactory postMessageFactory;

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
        User user = event.getSlashCommandInteraction()
                .getOptionUserValueByName("user")
                .orElse(event.getInteraction().getUser());

        postMessageFactory.createListPostFromFavorites(event, user);
    }
}