package post;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import post.favorites.FavoriteEvent;
import post.favorites.FavoritesMessage;
import post.history.HistoryEvent;
import post.history.HistoryMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PostMessages {

    private static final Map<Long, PostMessage> postMessageMap = new ConcurrentHashMap<>();

    public static void addPost(PostMessage postMessage) {
        postMessageMap.put(postMessage.getMessage().getId(), postMessage);
    }

    public static void removePost(PostMessage postMessage) {
        postMessageMap.remove(postMessage.getEvent().getInteraction().getId(), postMessage);
    }

    public static void handleInteraction(MessageComponentCreateEvent event) {
        Message message = event.getMessageComponentInteraction().getMessage();
        log.warn(String.valueOf(message.getId()));

        if (!postMessageMap.containsKey(message.getId())) {
            log.warn("Received buttonInteractionEvent for unknown message id {}", message.getId());
            return;
        } else {
            PostMessage postMessage = postMessageMap.get(message.getId());
            postMessage.handleInteraction(event);
        }
    }

    public static void onFavoriteEvent(FavoriteEvent favoriteEvent) {
        postMessageMap.values().stream()
                .filter(FavoritesMessage.class::isInstance)
                .map(FavoritesMessage.class::cast)
                .forEach(p -> p.onFavoriteEvent(favoriteEvent));
    }

    public static synchronized void onHistoryEvent(HistoryEvent historyEvent) {
        postMessageMap.values().stream()
                .filter(HistoryMessage.class::isInstance)
                .map(HistoryMessage.class::cast)
                .forEach(p -> p.onHistoryEvent(historyEvent));
    }
}
