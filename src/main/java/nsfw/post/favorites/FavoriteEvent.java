package nsfw.post.favorites;

import lombok.Getter;
import nsfw.post.PostResolvableEntry;
import org.javacord.api.entity.user.User;
import org.springframework.context.ApplicationEvent;

@Getter
public final class FavoriteEvent extends ApplicationEvent {
    private final User user;
    private final PostResolvableEntry addedPost;
    private final FavoriteEventType eventType;

    public FavoriteEvent(User user, PostResolvableEntry addedPost, FavoriteEventType eventType) {
        super(new Object());
        this.user = user;
        this.addedPost = addedPost;
        this.eventType = eventType;
    }

}
