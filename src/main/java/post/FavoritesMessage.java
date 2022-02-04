package post;

import db.PostRepository;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.User;
import embed.ErrorEmbed;
import post.api.PostFetchException;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FavoritesMessage extends PostListMessage {

    private final User user;

    public FavoritesMessage(List<PostResolvable> postList, User user, ChatInputInteractionEvent event) {
        super(postList, event);
        this.user = user;
    }

    @Override
    public List<LayoutComponent> getButtons() {
        return PostMessageButtons.actionRowFavorites();
    }

    @Override
    public Mono<Void> handleInteraction(ButtonInteractionEvent buttonInteractionEvent) {
        if (buttonInteractionEvent.getCustomId().equals("delete-favorite")) {
            return removeFavorite(buttonInteractionEvent);
        }

        return super.handleInteraction(buttonInteractionEvent);
    }

    private Mono<Void> removeFavorite(ButtonInteractionEvent buttonInteractionEvent) {
        User reactingUser = buttonInteractionEvent.getInteraction().getUser();

        if (!reactingUser.equals(user)) {
            return Mono.empty();
        }

        try {
            Optional<Post> optionalPost = getCurrentPost();

            if (optionalPost.isEmpty()) {
                return Mono.empty();
            }

            PostRepository.removeFavorite(reactingUser, optionalPost.get());
            return buttonInteractionEvent.reply("Successfully removed favorite.").withEphemeral(true);
        } catch (PostFetchException e) {
            return buttonInteractionEvent.reply().withEmbeds(ErrorEmbed.create("Error fetching post"));
        }  catch (SQLException e) {
            return buttonInteractionEvent.reply().withEmbeds(ErrorEmbed.create("Error removing favorite"));
        }
    }
}
