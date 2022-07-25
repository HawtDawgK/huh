package post;

import embed.ErrorEmbed;
import embed.PostEmbedOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageUpdater;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import post.api.PostFetchException;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class PostListMessage extends PostMessage {

    @Getter
    private final List<PostResolvableEntry> postList;

    protected PostListMessage(List<PostResolvableEntry> postList, SlashCommandCreateEvent event) {
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
            Message message = getMessage();

            Optional<Post> optionalPost = getCurrentPost();

            if (optionalPost.isEmpty()) {
                log.info("Post is empty");
                return;
            }

            Post currentPost = optionalPost.get();
            PostEmbedOptions postEmbedOptions = toPostEmbedOptions(currentPost, postList.get(getPage()));

            PostMessageable postMessageable = PostMessageable.fromPost(postEmbedOptions);

            MessageUpdater updater = message.createUpdater();
            if (postMessageable.getContent() != null) {
                updater.setContent(postMessageable.getContent());
            }
            if (postMessageable.getEmbed() != null) {
                updater.setEmbed(postMessageable.getEmbed());
            }

            updater.applyChanges().join();
        } catch (PostFetchException e) {
            log.error("Error fetching post while editing message", e);
        }
    }

    private PostEmbedOptions toPostEmbedOptions(Post post, PostResolvableEntry entry) {
        String description = "Favorites for " + getEvent().getInteraction().getUser().getMentionTag();
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
