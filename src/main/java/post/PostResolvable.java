package post;

import enums.PostSite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import post.api.PostFetchException;
import post.cache.PostCache;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class PostResolvable {

    private final long postId;

    @Getter
    private final PostSite postSite;

    public Optional<Post> resolve() throws PostFetchException {
        Post cachedPost = PostCache.get(this);

        if (cachedPost != null) {
            return Optional.of(cachedPost);
        }

        return postSite.getPostApi().fetchById(postId);
    }

    @Override
    public String toString() {
        return "PostResolvable{id=" + postId + ", postApi=" + postSite + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostResolvable that = (PostResolvable) o;
        return postId == that.postId && postSite == that.postSite;
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, postSite);
    }
}
