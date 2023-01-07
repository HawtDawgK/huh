package nsfw.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.enums.PostSite;
import nsfw.post.api.PostFetchOptions;

@Slf4j
@Getter
@RequiredArgsConstructor
public class PostApiMessage extends PostMessage {

    private final int count;
    private final String tags;
    private final PostSite postSite;

    @Override
    public PostFetchOptions getPostFetchOptions() {
        return PostFetchOptions.builder()
                .page((long) getPage())
                .tags(tags)
                .postSite(postSite)
                .build();
    }
}
