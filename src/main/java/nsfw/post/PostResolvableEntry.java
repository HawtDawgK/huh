package nsfw.post;

import nsfw.enums.PostSite;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Getter
public class PostResolvableEntry extends PostResolvable {

    private final Instant storedAt;

    public PostResolvableEntry(long postId, PostSite postSite, Instant storedAt) {
        super(postId, postSite);
        this.storedAt = storedAt;
    }

    public static PostResolvableEntry fromPostResolvable(PostResolvable postResolvable) {
        return new PostResolvableEntry(postResolvable.getPostId(), postResolvable.getPostSite(), Instant.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PostResolvableEntry that = (PostResolvableEntry) o;
        return Objects.equals(getStoredAt(), that.getStoredAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getStoredAt());
    }
}
