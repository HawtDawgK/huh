package nsfw.post.api.danbooru;

import nsfw.post.Post;
import nsfw.post.api.PostQueryResult;

import java.util.ArrayList;
import java.util.List;

public class DanbooruPostQueryResult extends ArrayList<DanbooruPost> implements PostQueryResult {

    @Override
    public int getCount() {
        return size();
    }

    @Override
    public List<Post> getPosts() {
        return new ArrayList<>(this);
    }
}
