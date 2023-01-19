package nsfw.post.favorites;

import lombok.RequiredArgsConstructor;
import nsfw.db.PostEntity;
import nsfw.db.PostRepository;
import nsfw.post.Post;
import org.javacord.api.entity.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritesService {

    private final PostRepository postRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    public boolean addFavorite(User user, Post post) {
        if (hasFavorite(user, post)) {
            return false;
        }

        PostEntity postEntity = new PostEntity();
        postEntity.setUserId(user.getId());
        postEntity.setPostId(post.getId());
        postEntity.setSite(post.getPostSite());

        postRepository.save(postEntity);

        applicationEventPublisher.publishEvent(new FavoriteEvent(user, postEntity, FavoriteEventType.ADDED));

        return true;
    }

    public boolean removeFavorite(User user, Post post) {
        if (!hasFavorite(user, post)) {
            return false;
        }

        PostEntity postEntity = new PostEntity();
        postEntity.setUserId(user.getId());
        postEntity.setPostId(post.getId());
        postEntity.setSite(post.getPostSite());

        postRepository.delete(postEntity);

        applicationEventPublisher.publishEvent(new FavoriteEvent(user, postEntity, FavoriteEventType.REMOVED));
        return true;
    }

    public List<PostEntity> getFavorites(long userId) {
        return postRepository.findByUserId(userId);
    }

    private boolean hasFavorite(User user, Post post) {
        return postRepository.existsByUserIdAndPostIdAndSite(user.getId(),
                post.getId(), post.getPostSite());
    }
}
