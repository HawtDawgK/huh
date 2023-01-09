package nsfw.post.favorites;

import lombok.RequiredArgsConstructor;
import nsfw.db.PostEntity;
import nsfw.db.PostRepository;
import nsfw.post.PostMapper;
import nsfw.post.PostResolvable;
import nsfw.post.PostResolvableEntry;
import org.javacord.api.entity.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritesService {

    private final PostRepository postRepository;

    private final PostMapper postMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    public boolean addFavorite(User user, PostResolvable postResolvable) {
        if (hasFavorite(user, postResolvable)) {
            return false;
        }

        PostEntity postEntity = postMapper.toPostEntity(postResolvable, user);
        postRepository.save(postEntity);

        PostResolvableEntry newEntry = new PostResolvableEntry(postResolvable.getPostId(),
                postResolvable.getPostSite(), Instant.now());
        applicationEventPublisher.publishEvent(new FavoriteEvent(user, newEntry, FavoriteEventType.ADDED));

        return true;
    }

    public boolean removeFavorite(User user, PostResolvable postResolvable) {
        if (!hasFavorite(user, postResolvable)) {
            return false;
        }

        postRepository.delete(postMapper.toPostEntity(postResolvable, user));

        PostResolvableEntry postResolvableEntry = PostResolvableEntry.fromPostResolvable(postResolvable);

        applicationEventPublisher.publishEvent(new FavoriteEvent(user, postResolvableEntry, FavoriteEventType.REMOVED));
        return true;
    }

    public List<PostResolvableEntry> getFavorites(long userId) {
        List<PostEntity> byUserId = postRepository.findByUserId(userId);
        return postMapper.fromPostEntities(byUserId);
    }

    private boolean hasFavorite(User user, PostResolvable postResolvable) {
        return postRepository.existsByUserIdAndPostIdAndSiteName(user.getId(),
                postResolvable.getPostId(), postResolvable.getPostSite());
    }
}
