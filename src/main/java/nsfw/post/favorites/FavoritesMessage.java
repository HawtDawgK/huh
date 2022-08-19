package nsfw.post.favorites;

import nsfw.post.*;
import nsfw.post.history.PostHistory;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import nsfw.post.api.PostFetchException;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class FavoritesMessage extends PostListMessage {

    private final User user;

    private final PostHistory postHistory;

    public FavoritesMessage(ApplicationContext applicationContext, List<PostResolvableEntry> postList,
                            User user, SlashCommandCreateEvent event) {
        super(applicationContext, postList, event);
        this.postHistory = applicationContext.getBean(PostHistory.class);
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
    public Post getCurrentPost() throws PostFetchException {
        Post currentPost = super.getCurrentPost();

        postHistory.addPost(getEvent().getInteraction().getChannel().orElseThrow(), currentPost);
        return currentPost;
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
                    .respond().join();
            return;
        }

        PostResolvableEntry postResolvableEntry = getPostList().get(getPage());
        getPostService().removeFavorite(reactingUser, postResolvableEntry);
        getPostMessageCache().onFavoriteEvent(new FavoriteEvent(user, postResolvableEntry, FavoriteEventType.REMOVED));
        event.getMessageComponentInteraction().createImmediateResponder()
                .setContent("Successfully removed favorite.")
                .setFlags(MessageFlag.EPHEMERAL)
                .respond().join();
    }

}
