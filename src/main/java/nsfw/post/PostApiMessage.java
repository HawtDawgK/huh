package nsfw.post;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nsfw.enums.PostSite;
import nsfw.post.api.PostFetchOptions;
import org.javacord.api.entity.channel.TextChannel;

@Slf4j
@Getter
public class PostApiMessage extends PostMessage {

    private final int count;
    private final String tags;
    private final PostSite postSite;
    private final TextChannel textChannel;

    public PostApiMessage(PostService postService, PostmessageableService postmessageableService, TextChannel textChannel, int count, String tags, PostSite postSite) {
        super(postService, postmessageableService);
        this.count = count;
        this.tags = tags;
        this.postSite = postSite;
        this.textChannel = textChannel;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public PostFetchResult getCurrentPost() {
        return getPostService().fetchPost(textChannel, getPostFetchOptions());
    }

    @Override
    public PostFetchOptions getPostFetchOptions() {
        return PostFetchOptions.builder()
                .page((long) getPage())
                .tags(tags)
                .postSite(postSite)
                .build();
    }
}
