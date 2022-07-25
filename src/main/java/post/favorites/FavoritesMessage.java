package post.favorites;

import db.PostRepository;
import embed.ErrorEmbed;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import post.*;
import post.api.PostFetchException;
import post.history.PostHistory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FavoritesMessage extends PostListMessage {

    private final User user;

    public FavoritesMessage(List<PostResolvableEntry> postList, User user, SlashCommandCreateEvent event) {
        super(postList, event);
        this.user = user;
    }

    @Override
    public String getTitle() {
        return "Favorites for " + user.getMentionTag();
    }

    @Override
    public List<HighLevelComponent> getButtons() {
        return PostMessageButtons.actionRowFavorites();
    }

    @Override
    public Optional<Post> getCurrentPost() throws PostFetchException {
        Optional<Post> optionalPost = super.getCurrentPost();

        optionalPost.ifPresent(post -> PostHistory.addPost(getEvent().getInteraction().getChannel().orElseThrow(), post));
        return optionalPost;
    }

    @Override
    public void handleInteraction(MessageComponentCreateEvent event) {
        MessageComponentInteraction interaction = event.getMessageComponentInteraction();

        if (interaction.getCustomId().equals("delete-favorite")) {
            removeFavorite(event);
        } else {
            super.handleInteraction(event);
        }
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

    private void removeFavorite(MessageComponentCreateEvent event) {
        User reactingUser = event.getInteraction().getUser();

        if (!reactingUser.equals(user)) {
            event.getMessageComponentInteraction().createImmediateResponder()
                    .setContent("Only the author can delete a favorite.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        try {
            PostResolvableEntry postResolvableEntry = getPostList().get(getPage());
            PostRepository.removeFavorite(reactingUser, postResolvableEntry);
            PostMessages.onFavoriteEvent(new FavoriteEvent(user, postResolvableEntry, FavoriteEventType.REMOVED));
            event.getMessageComponentInteraction().createImmediateResponder()
                    .setContent("Successfully removed favorite.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }  catch (SQLException e) {
            event.getMessageComponentInteraction().createImmediateResponder()
                    .addEmbed(ErrorEmbed.create("Error removing favorite"))
                    .respond();
        }
    }

}
