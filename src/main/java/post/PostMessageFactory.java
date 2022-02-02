package post;

import db.PostRepository;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import embed.ErrorEmbed;
import embed.PostNotFoundEmbed;
import enums.PostSite;

import lombok.extern.slf4j.Slf4j;
import post.api.PostApi;
import post.api.PostFetchException;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class PostMessageFactory {

    private PostMessageFactory() { }

    public static Mono<Void> createPost(ChatInputInteractionEvent event, String tags, PostSite postSite) throws PostFetchException {
        PostApi postApi = postSite.getPostApi();
        int count = postApi.fetchCount(tags);

        if (count == 0) {
            return event.reply().withEmbeds(PostNotFoundEmbed.create(tags));
        }

        PostMessage postMessage = new PostMessage(count, tags, event, postSite);
        postMessage.initReply();

        PostMessages.addPost(postMessage);

        return Mono.empty();
    }

    public static Mono<Void> createListPostFromFavorites(ChatInputInteractionEvent event) {
        User user = event.getInteraction().getUser();
        try {
            List<PostResolvable> favorites = PostRepository.getFavorites(user.getId().asLong());

            if (favorites.isEmpty()) {
                return event.reply().withEmbeds(ErrorEmbed.create("No posts in history"));
            }

            return createPostListMessage(favorites, event);
        } catch (SQLException e) {
            log.error("Error occurred fetching favorites", e);
            return event.reply().withEmbeds(ErrorEmbed.create("Error fetching favorites"));
        }
    }

    public static Mono<Void> createPostListMessage(List<PostResolvable> postResolvables, ChatInputInteractionEvent event) {
        PostMessage postMessage = new PostListMessage(postResolvables, null, event, PostSite.RULE34);
        postMessage.initReply();

        PostMessages.addPost(postMessage);

        return Mono.empty();
    }
}
