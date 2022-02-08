package post.cache;

import org.jetbrains.annotations.Nullable;
import post.Post;
import post.PostResolvableEntry;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PostCache {

    private static final Map<PostResolvableEntry, Post> POST_CACHE = new HashMap<>();

    public static void put(Post post) {
        POST_CACHE.put(new PostResolvableEntry(post.getId(), post.getSite(), Instant.now()), post);
    }

    public static @Nullable Post get(PostResolvableEntry postResolvable) {
        return POST_CACHE.get(postResolvable);
    }
}
