package post;

import enums.PostSite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import post.api.PostFetchException;
import post.cache.PostCache;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class PostResolvable {

    private final long postId;

    private final PostSite postSite;

    public Optional<Post> resolve() throws PostFetchException {
        Post cachedPost = PostCache.get(this);

        if (cachedPost != null) {
            return Optional.of(cachedPost);
        }

        return postSite.getPostApi().fetchById(postId);
    }
}
