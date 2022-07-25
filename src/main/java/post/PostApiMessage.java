package post;

import embed.ErrorEmbed;
import embed.PostEmbedOptions;
import embed.PostNotFoundEmbed;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import post.api.PostApi;
import post.api.PostFetchException;
import post.cache.PostCache;
import post.history.PostHistory;

import java.util.Optional;

public class PostApiMessage extends PostMessage {

    private final int count;
    private final String tags;
    private final PostApi postApi;

    public PostApiMessage(SlashCommandCreateEvent event, PostApi postApi, String tags, int count) {
        super(event);
        this.postApi = postApi;
        this.tags = tags;
        this.count = count;
    }

    @Override
    PostMessageable toPostMessageable() {
        try {
            Optional<Post> optionalPost = getCurrentPost();

            if (optionalPost.isEmpty()) {
                return PostMessageable.fromEmbed(PostNotFoundEmbed.create(tags));
            }

            Post currentPost = optionalPost.get();
            PostEmbedOptions postEmbedOptions = PostEmbedOptions.builder()
                    .post(currentPost)
                    .page(getPage())
                    .count(getCount())
                    .build();

            return PostMessageable.fromPost(postEmbedOptions);
        } catch (PostFetchException e) {
            return PostMessageable.fromEmbed(ErrorEmbed.create("Error fetching post for tags " + tags));
        }
    }

    @Override
    public Optional<Post> getCurrentPost() throws PostFetchException {
        Optional<Post> optionalPost = postApi.fetchByTagsAndPage(tags, getPage());

        optionalPost.ifPresent(post -> {
            PostCache.put(post);
            PostHistory.addPost(getEvent().getInteraction().getChannel().get(), post);
        });

        return optionalPost;
    }

    @Override
    public int getCount() {
        return count;
    }
}
