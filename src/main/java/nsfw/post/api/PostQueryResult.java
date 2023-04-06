package nsfw.post.api;

import nsfw.post.Post;

import java.util.List;

public interface PostQueryResult<P extends Post> extends CountResult {

    int getCount();

    List<P> getPosts();
}
