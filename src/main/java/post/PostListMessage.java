package post;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import embed.ErrorEmbed;
import embed.PostEmbedOptions;
import lombok.Getter;
import post.api.PostFetchException;

import java.util.List;
import java.util.Optional;

public class PostListMessage extends PostMessage {

    @Getter
    private final List<PostResolvableEntry> postList;

    public PostListMessage(List<PostResolvableEntry> postList, ChatInputInteractionEvent event) {
        super(event);
        this.postList = postList;
    }

    @Override
    public Optional<Post> getCurrentPost() throws PostFetchException {
        return postList.get(getPage()).resolve();
    }

    @Override
    PostMessageable toPostMessageable() {
        try {
            Optional<Post> optionalPost = getCurrentPost();

            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                PostResolvableEntry currentEntry = postList.get(getPage());

                String description = "Favorites for " + getEvent().getInteraction().getUser().getMention();
                PostEmbedOptions options = PostEmbedOptions.builder()
                        .post(post)
                        .entry(currentEntry)
                        .description(description)
                        .page(getPage())
                        .count(getCount())
                        .build();

                return PostMessageable.fromPost(options);
            }

            return PostMessageable.fromEmbed(ErrorEmbed.create("Could not fetch post"));
        } catch (PostFetchException e) {
            return PostMessageable.fromEmbed(ErrorEmbed.create("Error fetching post"));
        }
    }

    @Override
    public int getCount() {
        return postList.size();
    }
}
