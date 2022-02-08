package post;

import db.PostRepository;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.User;
import embed.ErrorEmbed;
import embed.PostNotFoundEmbed;
import enums.PostSite;

import lombok.extern.slf4j.Slf4j;
import post.api.PostApi;
import post.api.PostFetchException;
import post.favorites.FavoritesMessage;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class PostMessageFactory {

    private PostMessageFactory() { }

    public static Mono<Void> createPost(ChatInputInteractionEvent event, String tags, PostSite postSite) throws PostFetchException {
        PostApi postApi = postSite.getPostApi();
        int count = postApi.fetchCount(tags);

        if (count == 0) {
            return event.reply().withEmbeds(PostNotFoundEmbed.create(tags));
        }

        int maxCount = postApi.getMaxCount().map(c -> Math.min(c, count)).orElse(count);

        PostMessage postMessage = new PostApiMessage(event, postApi, tags, maxCount);
        postMessage.initReply();

        PostMessages.addPost(postMessage);

        return Mono.empty();
    }

    public static Mono<Void> createListPostFromFavorites(ChatInputInteractionEvent event, User user) {
        try {
            List<PostResolvableEntry> favorites = PostRepository.getFavorites(user.getId().asLong());

            if (favorites.isEmpty()) {
                return event.reply().withEmbeds(ErrorEmbed.create("No favorites found."));
            }

            PostMessage postMessage = new FavoritesMessage(favorites, user, event);
            postMessage.initReply();

            PostMessages.addPost(postMessage);

            return Mono.empty();
        } catch (SQLException e) {
            log.error("Error occurred fetching favorites", e);
            return event.reply().withEmbeds(ErrorEmbed.create("Error fetching favorites"));
        }
    }

}
