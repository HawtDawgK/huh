package nsfw.post;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.PostFetchOptions;

import java.util.Random;

@Slf4j
@Getter
@Setter
public abstract class PostMessage {

    private Post currentPost;

    private int page;

    private final Random random = new Random();

    public abstract int getCount();

    public abstract PostFetchOptions getPostFetchOptions();

    void nextPage() {
        page = Math.floorMod(page + 1, getCount());
    }

    void previousPage() {
        page = Math.floorMod(page - 1, getCount());
    }

    void randomPage() {
        page = random.nextInt(getCount());
    }
}
