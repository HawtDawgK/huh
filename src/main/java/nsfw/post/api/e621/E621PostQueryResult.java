package nsfw.post.api.e621;

import nsfw.post.Post;
import nsfw.post.api.PostQueryResult;

import java.util.Collections;
import java.util.List;

public class E621PostQueryResult implements PostQueryResult {

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public List<Post> getPosts() {
        return Collections.emptyList();
    }

}
