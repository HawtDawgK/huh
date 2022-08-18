package nsfw.post.api;

import nsfw.post.Post;

import java.util.List;

public interface PostQueryResult<P extends Post> {

    int getCount();

    int getOffset();

    List<P> getPosts();
}
