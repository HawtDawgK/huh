package post;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import embed.ErrorEmbed;
import embed.PostNotFoundEmbed;
import post.api.PostApi;
import post.api.PostFetchException;
import post.cache.PostCache;
import post.history.PostHistory;

import java.util.Optional;

public class PostApiMessage extends PostMessage {

    private final int count;
    private final String tags;
    private final PostApi postApi;

    public PostApiMessage(ChatInputInteractionEvent event, PostApi postApi, String tags, int count) {
        super(event);
        this.postApi = postApi;
        this.tags = tags;
        this.count = count;
    }

    @Override
    PostMessageable toPostMessageable() {
        try {
            return getCurrentPost().map(post -> PostMessageable.fromPost(post, getPage(), getCount()))
                    .orElseGet(() -> PostMessageable.fromEmbed(PostNotFoundEmbed.create(tags)));
        } catch (PostFetchException e) {
            return PostMessageable.fromEmbed(ErrorEmbed.create("Error fetching post for tags " + tags));
        }
    }

    @Override
    public Optional<Post> getCurrentPost() throws PostFetchException {
        Optional<Post> optionalPost = postApi.fetchByTagsAndPage(tags, getPage());

        optionalPost.ifPresent(post -> {
            PostCache.put(post);
            PostHistory.addPost(getEvent().getInteraction().getChannel().block(), post);
        });

        return optionalPost;
    }

    @Override
    public int getCount() {
        return count;
    }
}
