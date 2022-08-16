package nsfw.post;

import nsfw.embed.EmbedService;
import nsfw.embed.PostEmbedOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageUpdater;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import nsfw.post.api.PostFetchException;
import org.springframework.context.ApplicationContext;

import java.util.List;

@Slf4j
public abstract class PostListMessage extends PostMessage {

    @Getter
    private final List<PostResolvableEntry> postList;

    private final PostService postService;

    private final EmbedService embedService;

    protected PostListMessage(ApplicationContext applicationContext,
                              List<PostResolvableEntry> postList, SlashCommandCreateEvent event) {
        super(0, event, applicationContext);
        this.postList = postList;
        this.embedService = applicationContext.getBean(EmbedService.class);
        this.postService = applicationContext.getBean(PostService.class);
    }

    public abstract String getTitle();

    @Override
    public Post getCurrentPost() throws PostFetchException {
        return postService.resolve(postList.get(getPage()));
    }

    @Override
    PostMessageable toPostMessageable() {
        try {
            Post currentPost = getCurrentPost();

            PostResolvableEntry currentEntry = postList.get(getPage());
            PostEmbedOptions options = toPostEmbedOptions(currentPost, currentEntry);

            return PostMessageable.fromPost(options, embedService);
        } catch (PostFetchException e) {
            return PostMessageable.fromEmbed(embedService.createErrorEmbed("Error fetching post"));
        }
    }

    public void editMessage() {
        try {
            Message message = getMessage();

            Post currentPost = getCurrentPost();
            PostEmbedOptions postEmbedOptions = toPostEmbedOptions(currentPost, postList.get(getPage()));

            PostMessageable postMessageable = PostMessageable.fromPost(postEmbedOptions, embedService);

            MessageUpdater updater = message.createUpdater();
            updater.setContent(postMessageable.getContent());
            updater.setEmbed(postMessageable.getEmbed());

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
