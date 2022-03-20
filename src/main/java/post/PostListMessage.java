package post;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageEditSpec;
import embed.ErrorEmbed;
import embed.PostEmbedOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import post.api.PostFetchException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class PostListMessage extends PostMessage {

    @Getter
    private final List<PostResolvableEntry> postList;

    protected PostListMessage(List<PostResolvableEntry> postList, ChatInputInteractionEvent event) {
        super(event);
        this.postList = postList;
    }

    public abstract String getTitle();

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
                PostEmbedOptions options = toPostEmbedOptions(post, currentEntry);

                return PostMessageable.fromPost(options);
            }

            return PostMessageable.fromEmbed(ErrorEmbed.create("Could not fetch post"));
        } catch (PostFetchException e) {
            return PostMessageable.fromEmbed(ErrorEmbed.create("Error fetching post"));
        }
    }

    public void editMessage() {
        try {
            Message message = getEvent().getReply().block();

            if (message == null) {
                log.info("");
                return;
            }

            Optional<Post> optionalPost = getCurrentPost();

            if (optionalPost.isEmpty()) {
                log.info("Post is empty");
                return;
            }

            Post currentPost = optionalPost.get();
            PostEmbedOptions postEmbedOptions = toPostEmbedOptions(currentPost, postList.get(getPage()));

            PostMessageable postMessageable = PostMessageable.fromPost(postEmbedOptions);

            MessageEditSpec.Builder builder = MessageEditSpec.builder()
                    .contentOrNull(postMessageable.getContent());

            if (postMessageable.getEmbed() != null) {
                builder.embeds(Collections.singletonList(postMessageable.getEmbed()));
            }

            message.edit(builder.build()).block();
        } catch (PostFetchException e) {
            log.error("Error fetching post while editing message", e);
        }
    }

    private PostEmbedOptions toPostEmbedOptions(Post post, PostResolvableEntry entry) {
        String description = "Favorites for " + getEvent().getInteraction().getUser().getMention();
        return PostEmbedOptions.builder()
                .post(post)
                .entry(entry)
                .description(description)
                .page(getPage())
                .count(getCount())
                .build();
    }

    @Override
    public int getCount() {
        return postList.size();
    }

}
