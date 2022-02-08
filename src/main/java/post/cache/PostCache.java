package post.cache;

import org.jetbrains.annotations.Nullable;
import post.Post;
import post.PostResolvable;

import java.util.HashMap;
import java.util.Map;

public class PostCache {

    private static final Map<PostResolvable, Post> POST_CACHE = new HashMap<>();

    public static void put(Post post) {
        POST_CACHE.put(new PostResolvable(post.getId(), post.getSite()), post);
    }

    public static @Nullable Post get(PostResolvable postResolvable) {
        return POST_CACHE.get(postResolvable);
    }
}
