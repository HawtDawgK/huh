package nsfw.post.history;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nsfw.db.PostEntity;
import nsfw.post.PostFetchResult;
import nsfw.post.PostService;
import nsfw.post.list.PostListMessage;
import org.javacord.api.entity.channel.TextChannel;

import java.util.List;

@Slf4j
@Getter
public class HistoryMessage extends PostListMessage {

    private final TextChannel textChannel;

    public HistoryMessage(PostService postService, List<PostEntity> posts, TextChannel textChannel) {
        super(postService, posts);
        this.textChannel = textChannel;
    }

    public String getTitle() {
        return "Post history";
    }

    @Override
    public PostFetchResult getCurrentPost() {
        if (getPosts().isEmpty()) {
            return new PostFetchResult(null, true, "No posts in history");
        }

        return getPostService().fetchPost(textChannel, getPostFetchOptions());
    }

    public void onHistoryEvent(HistoryEvent event) {
        if (textChannel.equals(event.getChannel())) {
            getPosts().add(event.getNewEntry());
        }
    }

}
