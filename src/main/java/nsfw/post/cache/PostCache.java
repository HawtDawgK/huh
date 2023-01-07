package nsfw.post.cache;

import nsfw.post.Post;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import nsfw.post.PostResolvable;

import java.util.HashMap;
import java.util.Map;

@Component
public class PostCache {

    private static final Map<PostResolvable, Post> POST_CACHE = new HashMap<>();

    public void put(Post post) {
//        POST_CACHE.put(new PostResolvable(post.getId(), post.getSite()), post);
    }

    public @Nullable Post get(PostResolvable postResolvable) {
        return POST_CACHE.get(postResolvable);
    }
}
