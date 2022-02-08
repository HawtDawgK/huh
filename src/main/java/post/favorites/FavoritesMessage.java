package post.favorites;

import db.PostRepository;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.User;
import embed.ErrorEmbed;
import post.PostListMessage;
import post.PostMessageButtons;
import post.PostResolvableEntry;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;

public class FavoritesMessage extends PostListMessage {

    private final User user;

    public FavoritesMessage(List<PostResolvableEntry> postList, User user, ChatInputInteractionEvent event) {
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
            PostResolvableEntry postResolvableEntry = getPostList().get(getPage());
            PostRepository.removeFavorite(reactingUser, postResolvableEntry);
            return buttonInteractionEvent.reply("Successfully removed favorite.").withEphemeral(true);
        }  catch (SQLException e) {
            return buttonInteractionEvent.reply().withEmbeds(ErrorEmbed.create("Error removing favorite"));
        }
    }
}
