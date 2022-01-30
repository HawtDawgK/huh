package post;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import post.api.PostApi;
import java.util.List;
import java.util.Optional;

public class PostListMessage extends PostMessage {

    private final List<PostResolvable> postList;

    public PostListMessage(List<PostResolvable> postList, String tags, ChatInputInteractionEvent event, PostApi postApi) {
        super(postList.size(), tags, event, postApi);
        this.postList = postList;
    }

    @Override
    Optional<Post> getCurrentPost() {
        return postList.get(getPage()).resolve();
    }

}
