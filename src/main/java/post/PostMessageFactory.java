package post;

import db.PostRepository;
import embed.ErrorEmbed;
import embed.PostNotFoundEmbed;
import enums.PostSite;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import post.api.PostApi;
import post.api.PostFetchException;
import post.favorites.FavoritesMessage;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@UtilityClass
public class PostMessageFactory {

    public static void createPost(SlashCommandCreateEvent event, String tags, PostSite postSite) throws PostFetchException {
        PostApi postApi = postSite.getPostApi();
        int count = postApi.fetchCount(tags);

        if (count == 0) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(PostNotFoundEmbed.create(tags))
                    .respond();
            return;
        }

        int maxCount = postApi.getMaxCount().map(c -> Math.min(c, count)).orElse(count);

        PostMessage postMessage = new PostApiMessage(event, postApi, tags, maxCount);
        postMessage.initReply();

        PostMessages.addPost(postMessage);
    }

    public static void createListPostFromFavorites(SlashCommandCreateEvent event, User user) {
        try {
            List<PostResolvableEntry> favorites = PostRepository.getFavorites(user.getId());

            if (favorites.isEmpty()) {
                event.getSlashCommandInteraction().createImmediateResponder()
                        .addEmbeds(ErrorEmbed.create("No favorites found."))
                        .respond();
                return;
            }

            PostMessage postMessage = new FavoritesMessage(favorites, user, event);
            postMessage.initReply();

            PostMessages.addPost(postMessage);
        } catch (SQLException e) {
            log.error("Error occurred fetching favorites", e);
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(ErrorEmbed.create("Error fetching favorites"))
                    .respond();
        }
    }

}
