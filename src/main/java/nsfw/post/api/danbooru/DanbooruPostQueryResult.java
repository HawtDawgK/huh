package nsfw.post.api.danbooru;

import nsfw.post.api.PostQueryResult;

import java.util.ArrayList;
import java.util.List;

public class DanbooruPostQueryResult extends ArrayList<DanbooruPost> implements PostQueryResult<DanbooruPost> {

    @Override
    public int getCount() {
        return size();
    }

    @Override
    public List<DanbooruPost> getPosts() {
        return new ArrayList<>(this);
    }
}
