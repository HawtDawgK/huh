package nsfw.post.favorites;

import lombok.RequiredArgsConstructor;
import nsfw.post.PostMessage;
import nsfw.post.PostResolvableEntry;
import nsfw.post.api.PostFetchOptions;
import org.javacord.api.entity.user.User;

import java.util.List;

@RequiredArgsConstructor
public class FavoritesMessage extends PostMessage {

    private final User user;

    private final List<PostResolvableEntry> posts;

    public String getTitle() {
        return "Favorites for " + user.getMentionTag();
    }

    public void onFavoriteEvent(FavoriteEvent favoriteEvent) {
        if (!favoriteEvent.getUser().equals(user)) {
            return;
        }

        if (favoriteEvent.getEventType() == FavoriteEventType.ADDED) {
            posts.add(favoriteEvent.getAddedPost());
        } else {
            int oldSize = posts.size();
            posts.remove(favoriteEvent.getAddedPost());
            if (oldSize - 1 == getPage()) {
                setPage(getPage() - 1);
            }
        }
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public PostFetchOptions getPostFetchOptions() {
        PostResolvableEntry currentEntry = posts.get(getPage());

        return PostFetchOptions.builder()
                .postSite(currentEntry.getPostSite())
                .id(currentEntry.getPostId())
                .build();
    }

}
