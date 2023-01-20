package nsfw.post.cache;

import nsfw.db.PostEntity;
import nsfw.post.Post;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PostCache {

    private static final Map<PostEntity, Post> POST_CACHE = new HashMap<>();

    public void put(Post post) {
        PostEntity postEntity = new PostEntity();
        postEntity.setPostId(post.getId());
        postEntity.setSite(post.getPostSite());

        POST_CACHE.put(postEntity, post);
    }

    public @Nullable Post get(PostEntity postEntity) {
        return POST_CACHE.get(postEntity);
    }
}
