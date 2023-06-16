package nsfw.post.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.commands.CommandException;
import nsfw.enums.PostSite;
import nsfw.post.PostService;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.messageable.PostmessageableService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageFactory {

    private final PostService postService;

    private final PostMessageCache postMessageCache;

    private final PostMessageService postMessageService;

    private final PostmessageableService postmessageableService;

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
        PostMessage postMessage = new PostApiMessage(postService, postmessageableService, textChannel, maxCount, tags, postSite);

        postMessageService.addPost(event, postMessage);
    }

}
