package post.favorites;

import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import post.PostResolvableEntry;

@Getter
@RequiredArgsConstructor
public class FavoriteEvent {

    private final User user;

    private final PostResolvableEntry addedPost;

    private final FavoriteEventType eventType;

}
