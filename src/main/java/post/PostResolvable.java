package post;

import lombok.RequiredArgsConstructor;
import post.api.PostApi;
import post.api.PostFetchException;

import java.util.Optional;

@RequiredArgsConstructor
public class PostResolvable {

    private final long id;

    private final PostApi postApi;

    public Optional<Post> resolve() throws PostFetchException {
        return postApi.fetchById(id);
    }

    @Override
    public String toString() {
        return "PostResolvable{id=" + id + ", postApi=" + postApi + '}';
    }
}
