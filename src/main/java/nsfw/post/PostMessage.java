package nsfw.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nsfw.embed.PostEmbedOptions;
import nsfw.post.api.PostFetchOptions;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public abstract class PostMessage {

    private String uuid = UUID.randomUUID().toString();

    private Post currentPost;

    private int page;

    private final Random random = new Random();

    private final PostService postService;

    private final PostmessageableService postmessageableService;

    public abstract int getCount();

    public abstract PostFetchOptions getPostFetchOptions();

    public abstract String getTitle();

    public abstract PostFetchResult getCurrentPost();

    void nextPage() {
        if (getCount() != 0) {
            page = Math.floorMod(page + 1, getCount());
        }
    }

    void previousPage() {
        if (getCount() != 0) {
            page = Math.floorMod(page - 1, getCount());
        }
    }

    void randomPage() {
        if (getCount() != 0) {
            page = random.nextInt(getCount());
        }
    }

    public PostMessageable toPostMessageable() {
        PostEmbedOptions postEmbedOptions = PostEmbedOptions.builder()
                .postFetchResult(getCurrentPost())
                .title(getTitle())
                .page(getPage())
                .count(getCount())
                .build();

        return postmessageableService.fromPost(postEmbedOptions);
    }
}
