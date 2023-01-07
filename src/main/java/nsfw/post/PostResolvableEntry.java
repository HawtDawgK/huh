package nsfw.post;

import nsfw.enums.PostSite;
import lombok.Getter;

import java.time.Instant;

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

}
