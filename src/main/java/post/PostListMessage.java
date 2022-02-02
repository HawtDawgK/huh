package post;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import enums.PostSite;
import post.api.PostFetchException;

import java.util.List;
import java.util.Optional;

public class PostListMessage extends PostMessage {

    private final List<PostResolvable> postList;

    public PostListMessage(List<PostResolvable> postList, String tags, ChatInputInteractionEvent event, PostSite postSite) {
        super(postList.size(), tags, event, postSite);
        this.postList = postList;
    }

    @Override
    Optional<Post> getCurrentPost() throws PostFetchException {
        return postList.get(getPage()).resolve();
    }

}
