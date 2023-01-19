package nsfw.post.favorites;

import nsfw.db.PostEntity;
import nsfw.post.list.PostListMessage;
import org.javacord.api.entity.user.User;

import java.util.List;

public class FavoritesMessage extends PostListMessage {

    private final User user;

    public FavoritesMessage(User user, List<PostEntity> posts) {
        super(posts);
        this.user = user;
    }

    public String getTitle() {
        return "Favorites for " + user.getMentionTag();
    }

    public void onFavoriteEvent(FavoriteEvent favoriteEvent) {
        if (!favoriteEvent.getUser().equals(user)) {
            return;
        }

        if (favoriteEvent.getEventType() == FavoriteEventType.ADDED) {
            getPosts().add(favoriteEvent.getAddedPost());
        } else {
            int oldSize = getPosts().size();
            getPosts().remove(favoriteEvent.getAddedPost());
            if (oldSize - 1 == getPage()) {
                setPage(getPage() - 1);
            }
        }
    }
}
