package nsfw.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.commands.CommandException;
import nsfw.enums.PostSite;
import nsfw.post.api.PostFetchOptions;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageFactory {

    private final PostService postService;

    private final PostMessageCache postMessageCache;

    public void createPost(SlashCommandCreateEvent event, String tags, PostSite postSite) throws CommandException {
        PostFetchOptions postFetchOptions = PostFetchOptions.builder()
                .postSite(postSite)
                .tags(tags)
                .counts(true)
                .build();
        int count = postService.fetchCount(postFetchOptions);

        if (count == 0) {
            throw new CommandException("No posts found for " + tags);
        }

        int maxCount = postSite.getPostApi().getMaxCount().map(c -> Math.min(c, count)).orElse(count);

        TextChannel textChannel = event.getInteraction().getChannel().orElseThrow(() -> new CommandException("No channel"));
        PostMessage postMessage = new PostApiMessage(postService, textChannel, maxCount, tags, postSite);

        postMessageCache.addPost(event, postMessage);
    }

}
