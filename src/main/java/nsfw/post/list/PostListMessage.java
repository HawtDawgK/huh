package nsfw.post.list;

import lombok.Getter;
import nsfw.db.PostEntity;
import nsfw.post.PostMessage;
import nsfw.post.PostService;
import nsfw.post.api.PostFetchOptions;

import java.util.List;

@Getter
public abstract class PostListMessage extends PostMessage {

    private final List<PostEntity> posts;

    protected PostListMessage(PostService postService, List<PostEntity> posts) {
        super(postService);
        this.posts = posts;
    }

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

}
