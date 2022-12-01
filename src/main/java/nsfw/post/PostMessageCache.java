package nsfw.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.springframework.stereotype.Component;
import nsfw.post.favorites.FavoriteEvent;
import nsfw.post.favorites.FavoritesMessage;
import nsfw.post.history.HistoryEvent;
import nsfw.post.history.HistoryMessage;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageCache {

    private final DiscordApi discordApi;

    private static final Map<Long, PostMessage> postMessageMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void postConstruct() {
        discordApi.addMessageComponentCreateListener(this::handleInteraction);
    }

    public void addPost(PostMessage postMessage) {
        postMessageMap.put(postMessage.getMessage().getId(), postMessage);
    }

    public void removePost(PostMessage postMessage) {
        postMessageMap.remove(postMessage.getEvent().getInteraction().getId(), postMessage);
    }

    public void handleInteraction(MessageComponentCreateEvent event) {
        Message message = event.getMessageComponentInteraction().getMessage();
        log.warn(String.valueOf(message.getId()));

        if (postMessageMap.containsKey(message.getId())) {
            PostMessage postMessage = postMessageMap.get(message.getId());
            postMessage.handleInteraction(event);
        } else {
            log.warn("Received buttonInteractionEvent for unknown message id {}", message.getId());
        }
    }

    public void onFavoriteEvent(FavoriteEvent favoriteEvent) {
        postMessageMap.values().stream()
                .filter(FavoritesMessage.class::isInstance)
                .map(FavoritesMessage.class::cast)
                .forEach(p -> p.onFavoriteEvent(favoriteEvent));
    }

    public synchronized void onHistoryEvent(HistoryEvent historyEvent) {
        postMessageMap.values().stream()
                .filter(HistoryMessage.class::isInstance)
                .map(HistoryMessage.class::cast)
                .forEach(p -> p.onHistoryEvent(historyEvent));
    }
}
