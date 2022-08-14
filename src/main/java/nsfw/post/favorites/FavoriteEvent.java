package nsfw.post.favorites;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.user.User;
import nsfw.post.PostResolvableEntry;

@Getter
@RequiredArgsConstructor
public class FavoriteEvent {

    private final User user;

    private final PostResolvableEntry addedPost;

    private final FavoriteEventType eventType;

}
