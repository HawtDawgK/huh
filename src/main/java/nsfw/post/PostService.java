package nsfw.post;

import nsfw.db.PostEntity;
import nsfw.db.PostRepository;
import nsfw.post.api.PostFetchException;
import nsfw.post.cache.PostCache;
import org.javacord.api.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostCache postCache;

    public boolean hasFavorite(User user, PostResolvable postResolvable) {
        return postRepository.existsByUserIdAndPostIdAndSiteName(user.getId(),
                postResolvable.getPostId(), postResolvable.getPostSite());
    }

    public void addFavorite(User user, PostResolvable postResolvable) {
        PostEntity postEntity = postMapper.toPostEntity(postResolvable, user);
        postRepository.save(postEntity);
    }

    public void removeFavorite(User user, PostResolvable postResolvable) {
        postRepository.delete(postMapper.toPostEntity(postResolvable, user));
    }

    public List<PostResolvableEntry> getFavorites(long userId) {
        List<PostEntity> byUserId = postRepository.findByUserId(userId);

        return postMapper.fromPostEntities(byUserId);
    }

    public Post resolve(PostResolvableEntry postResolvable) throws PostFetchException {
        Post cachedPost = postCache.get(postResolvable);

        if (cachedPost != null) {
            return cachedPost;
        }

        return postResolvable.getPostSite().getPostApi().fetchById(postResolvable.getPostId());
    }

}
