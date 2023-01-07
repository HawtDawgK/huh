package nsfw.post;

import lombok.RequiredArgsConstructor;
import nsfw.post.favorites.FavoritesMessage;
import nsfw.post.favorites.FavoritesService;
import nsfw.embed.EmbedService;
import nsfw.enums.PostSite;

import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.PostFetchOptions;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import nsfw.post.api.PostFetchException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageFactory {

    private final EmbedService embedService;

    private final PostService postService;

    private final FavoritesService favoritesService;

    private final PostMessageCache postMessageCache;

    public void createPost(SlashCommandCreateEvent event, String tags, PostSite postSite) throws PostFetchException {
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

    public void createListPostFromFavorites(SlashCommandCreateEvent event, User user) {
        List<PostResolvableEntry> favorites = favoritesService.getFavorites(user.getId());

        // TODO: Handle empty favorites
        if (favorites.isEmpty()) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbeds(embedService.createErrorEmbed("No favorites found."))
                    .respond().join();
        } else {
            PostMessage postMessage = new FavoritesMessage(user, favorites);
            postMessageCache.addPost(event, postMessage);
        }
    }

}
