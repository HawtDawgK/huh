package nsfw.post.api.e621;

import nsfw.post.api.PostQueryResult;

import java.util.Collections;
import java.util.List;

public class E621PostQueryResult implements PostQueryResult<E621Post> {

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public List<E621Post> getPosts() {
        return Collections.emptyList();
    }

}
