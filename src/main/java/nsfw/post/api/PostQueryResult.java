package nsfw.post.api;

import nsfw.post.Post;

import java.util.List;

public interface PostQueryResult extends CountResult {

    int getCount();

    List<Post> getPosts();
}
