package post;


import java.util.List;
import java.util.Optional;

public interface PostQueryResult {

    int getCount();

    int getOffset();

    List<Post> getPosts();

    default Optional<Post> getFirstPost() {
        return getPosts().stream().findFirst();
    }
}
