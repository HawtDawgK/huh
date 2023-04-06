package nsfw.post.favorites;

import lombok.Getter;
import nsfw.db.PostEntity;
import nsfw.post.PostFetchResult;
import nsfw.post.PostService;
import nsfw.post.messageable.PostmessageableService;
import nsfw.post.list.PostListMessage;
import org.javacord.api.entity.user.User;

import java.util.List;

@Getter
public class FavoritesMessage extends PostListMessage {

    private final User user;

    public FavoritesMessage(PostService postService, PostmessageableService postmessageableService, User user, List<PostEntity> posts) {
        super(postService, postmessageableService, posts);
        this.user = user;
    }

    public String getTitle() {
        return "Favorites for " + user.getName();
    }

    @Override
    public PostFetchResult getCurrentPost() {
        if (getPosts().isEmpty()) {
            return new PostFetchResult(null, true, "No posts in favorites");
        }

        return getPostService().fetchPost(null, getPostFetchOptions());
    }

    public void onFavoriteEvent(FavoriteEvent favoriteEvent) {
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
