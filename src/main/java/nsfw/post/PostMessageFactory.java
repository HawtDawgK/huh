package nsfw.post;

import lombok.RequiredArgsConstructor;
import nsfw.embed.EmbedService;
import nsfw.enums.PostSite;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import nsfw.post.api.PostFetchException;
import nsfw.post.favorites.FavoritesMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageFactory {

    private final EmbedService embedService;

    private final PostService postService;

    private final PostMessages postMessages;

    private final ApplicationContext applicationContext;

    public void createPost(SlashCommandCreateEvent event, String tags, PostSite postSite) throws PostFetchException {
        int count = postSite.getPostApi().fetchCount(tags);

        if (count == 0) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(embedService.createNoPostsFoundEmbed(tags))
                    .respond();
            return;
        }

        int maxCount = postSite.getPostApi().getMaxCount().map(c -> Math.min(c, count)).orElse(count);

        PostMessage postMessage = new PostApiMessage(applicationContext, event, postSite.getPostApi(), tags, maxCount);
        postMessage.initReply();

        postMessages.addPost(postMessage);
    }

    public void createListPostFromFavorites(SlashCommandCreateEvent event, User user) {
        List<PostResolvableEntry> favorites = postService.getFavorites(user.getId());

        if (favorites.isEmpty()) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbeds(embedService.createErrorEmbed("No favorites found."))
                    .respond();
        } else {
            PostMessage postMessage = new FavoritesMessage(applicationContext, favorites, user, event);
            postMessage.initReply();

            postMessages.addPost(postMessage);
        }
    }

}
