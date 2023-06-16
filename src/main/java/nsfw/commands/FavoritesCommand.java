package nsfw.commands;

import lombok.RequiredArgsConstructor;
import nsfw.db.PostEntity;
import nsfw.post.message.PostMessage;
import nsfw.post.PostService;
import nsfw.post.message.PostMessageService;
import nsfw.post.messageable.PostmessageableService;
import nsfw.post.favorites.FavoritesMessage;
import nsfw.post.favorites.FavoritesService;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FavoritesCommand implements Command {

    private final FavoritesService favoritesService;

    private final PostService postService;

    private final PostmessageableService postmessageableService;

    private final PostMessageService postMessageService;

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

        PostMessage postMessage = new FavoritesMessage(postService, postmessageableService, user, favorites);
        postMessageService.addPost(event, postMessage);
    }
}
