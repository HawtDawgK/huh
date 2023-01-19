package nsfw.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.embed.EmbedService;
import nsfw.enums.PostSite;
import nsfw.post.api.PostFetchOptions;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageFactory {

    private final EmbedService embedService;

    private final PostService postService;

    private final PostMessageCache postMessageCache;

    public void createPost(SlashCommandCreateEvent event, String tags, PostSite postSite) {
        PostFetchOptions postFetchOptions = PostFetchOptions.builder()
                .postSite(postSite)
                .tags(tags)
                .counts(true)
                .build();
        int count = postService.fetchCount(postFetchOptions);

        if (count == 0) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(embedService.createNoPostsFoundEmbed(tags))
                    .respond().join();
            return;
        }

        int maxCount = postSite.getPostApi().getMaxCount().map(c -> Math.min(c, count)).orElse(count);

        PostMessage postMessage = new PostApiMessage(maxCount, tags, postSite);

        postMessageCache.addPost(event, postMessage);
    }

}
