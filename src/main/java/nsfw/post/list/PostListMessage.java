package nsfw.post.list;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nsfw.db.PostEntity;
import nsfw.post.PostMessage;
import nsfw.post.api.PostFetchOptions;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class PostListMessage extends PostMessage {

    private final List<PostEntity> posts;

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public PostFetchOptions getPostFetchOptions() {
        PostEntity currentEntry = posts.get(getPage());

        return PostFetchOptions.builder()
                .postSite(currentEntry.getSite())
                .id(currentEntry.getPostId())
                .build();
    }

    public abstract String getErrorMessage();
}
