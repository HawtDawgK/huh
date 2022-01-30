package post;

import lombok.RequiredArgsConstructor;
import post.api.PostApi;

import java.util.Optional;

@RequiredArgsConstructor
public class PostResolvable {

    private final long id;

    private final PostApi postApi;

    public Optional<Post> resolve() {
        return postApi.fetchById(id);
    }
}
