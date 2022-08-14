package nsfw.post;

import lombok.extern.slf4j.Slf4j;
import nsfw.embed.EmbedService;
import nsfw.embed.PostEmbedOptions;
import nsfw.post.cache.PostCache;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import nsfw.post.api.PostApi;
import nsfw.post.api.PostFetchException;
import nsfw.post.history.PostHistory;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@Slf4j
public class PostApiMessage extends PostMessage {

    private EmbedService embedService;

    private PostCache postCache;

    private PostHistory postHistory;

    private final int count;
    private final String tags;
    private final PostApi postApi;

    public PostApiMessage(ApplicationContext applicationContext, SlashCommandCreateEvent event, PostApi postApi, String tags, int count) {
        super(0, event, applicationContext);
        this.postApi = postApi;
        this.tags = tags;
        this.count = count;
        this.embedService = applicationContext.getBean(EmbedService.class);
        this.postCache = applicationContext.getBean(PostCache.class);
        this.postHistory = applicationContext.getBean(PostHistory.class);
    }

    @Override
    public Post getCurrentPost() throws PostFetchException {
        Optional<Post> optionalPost = postApi.fetchByTagsAndPage(tags, getPage());

        optionalPost.ifPresent(post -> {
            postCache.put(post);
            getEvent().getSlashCommandInteraction().getChannel().ifPresent(ch ->  postHistory.addPost(ch, post));
//            postHistory.addPost(getEvent().getSlashCommandInteraction().getChannel().get());
        });

        return optionalPost.orElseThrow(() -> new PostFetchException("post not found"));
    }

    @Override
    PostMessageable toPostMessageable() {
        try {
            Post currentPost = getCurrentPost();

            PostEmbedOptions postEmbedOptions = PostEmbedOptions.builder()
                    .post(currentPost)
                    .page(getPage())
                    .count(getCount())
                    .build();

            return PostMessageable.fromPost(postEmbedOptions, embedService);
        } catch (PostFetchException e) {
            log.error(e.getMessage(), e);
            return PostMessageable.fromEmbed(embedService.createErrorEmbed("Error fetching post for tags " + tags));
        }
    }

    @Override
    public int getCount() {
        return count;
    }
}
