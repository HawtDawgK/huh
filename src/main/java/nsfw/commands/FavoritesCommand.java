package nsfw.commands;

import lombok.RequiredArgsConstructor;
import nsfw.db.PostEntity;
import nsfw.embed.EmbedService;
import nsfw.post.PostMessage;
import nsfw.post.PostMessageCache;
import nsfw.post.favorites.FavoritesMessage;
import nsfw.post.favorites.FavoritesService;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FavoritesCommand implements Command {

    private final FavoritesService favoritesService;

    private final EmbedService embedService;

    private final PostMessageCache postMessageCache;

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
    public void apply(SlashCommandCreateEvent event) {
        User user = event.getSlashCommandInteraction()
                .getOptionByName("user")
                .flatMap(SlashCommandInteractionOption::getUserValue)
                .orElse(event.getInteraction().getUser());

        List<PostEntity> favorites = favoritesService.getFavorites(user.getId());

        // TODO: Handle empty favorites
        if (favorites.isEmpty()) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbeds(embedService.createErrorEmbed("No favorites found."))
                    .respond().join();
        } else {
            PostMessage postMessage = new FavoritesMessage(user, favorites);
            postMessageCache.addPost(event, postMessage);
        }
    }
}
