package post.favorites;

import db.PostRepository;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.User;
import embed.ErrorEmbed;
import post.*;
import post.api.PostFetchException;
import post.history.PostHistory;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
    public Optional<Post> getCurrentPost() throws PostFetchException {
        Optional<Post> optionalPost = super.getCurrentPost();

        optionalPost.ifPresent(post -> PostHistory.addPost(getEvent().getInteraction().getChannel().block(), post));
        return optionalPost;
    }

    @Override
    public Mono<Void> handleInteraction(ButtonInteractionEvent buttonInteractionEvent) {
        if (buttonInteractionEvent.getCustomId().equals("delete-favorite")) {
            return removeFavorite(buttonInteractionEvent);
        }

        return super.handleInteraction(buttonInteractionEvent);
    }

    public void onFavoriteEvent(FavoriteEvent favoriteEvent) {
        if (!favoriteEvent.getUser().equals(user)) {
            return;
        }

        if (favoriteEvent.getEventType() == FavoriteEventType.ADDED) {
            getPostList().add(favoriteEvent.getAddedPost());
        } else {
            int oldSize = getPostList().size();
            getPostList().remove(favoriteEvent.getAddedPost());
            if (oldSize - 1 == getPage()) {
                setPage(getPage() - 1);
            }
        }

        editMessage();
    }

    private Mono<Void> removeFavorite(ButtonInteractionEvent buttonInteractionEvent) {
        User reactingUser = buttonInteractionEvent.getInteraction().getUser();

        if (!reactingUser.equals(user)) {
            return buttonInteractionEvent
                    .reply("Only the author can delete a favorite.")
                    .withEphemeral(true);
        }

        try {
            PostResolvableEntry postResolvableEntry = getPostList().get(getPage());
            PostRepository.removeFavorite(reactingUser, postResolvableEntry);
            PostMessages.onFavoriteEvent(new FavoriteEvent(user, postResolvableEntry, FavoriteEventType.REMOVED));
            return buttonInteractionEvent.reply("Successfully removed favorite.").withEphemeral(true);
        }  catch (SQLException e) {
            return buttonInteractionEvent.reply().withEmbeds(ErrorEmbed.create("Error removing favorite"));
        }
    }

}
