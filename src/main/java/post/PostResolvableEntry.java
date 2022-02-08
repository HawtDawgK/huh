package post;

import enums.PostSite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import post.api.PostFetchException;
import post.cache.PostCache;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class PostResolvableEntry {

    private final long postId;

    private final PostSite postSite;

    private final Instant storedAt;

    public Optional<Post> resolve() throws PostFetchException {
        Post cachedPost = PostCache.get(this);

        if (cachedPost != null) {
            return Optional.of(cachedPost);
        }

        return postSite.getPostApi().fetchById(postId);
    }

    @Override
    public String toString() {
        return "PostResolvableEntry{" +
                "postId=" + postId +
                ", postSite=" + postSite +
                ", storedAt=" + storedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostResolvableEntry that = (PostResolvableEntry) o;
        return postId == that.postId && postSite == that.postSite && Objects.equals(storedAt, that.storedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, postSite, storedAt);
    }
}
