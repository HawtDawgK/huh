package post.api;

import post.Post;

import java.util.Optional;

public interface PostApi {

    Optional<Post> fetchById(long id);

    Optional<Post> fetchByTagsAndPage(String tags, int page);

    int fetchCount(String tags);

    int getMaxCount();
}
