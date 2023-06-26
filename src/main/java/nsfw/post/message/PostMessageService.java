package nsfw.post.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.PostFetchResult;
import nsfw.post.favorites.FavoriteEvent;
import nsfw.post.favorites.FavoritesMessage;
import nsfw.post.favorites.FavoritesService;
import nsfw.post.history.HistoryEvent;
import nsfw.post.history.HistoryMessage;
import nsfw.post.messageable.PostMessageable;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostMessageService {

    private final PostMessageCache postMessageCache;

    private final DiscordApi discordApi;

    private final FavoritesService favoritesService;

    @PostConstruct
    public void postConstruct() {
        discordApi.addMessageComponentCreateListener(this::handleInteraction);
    }

    public void handleInteraction(MessageComponentCreateEvent event) {
        long messageId = event.getMessageComponentInteraction().getMessage().getId();
        boolean messagePresent = postMessageCache.getPostMessageMap().containsKey(messageId);

        if (!messagePresent) {
            log.error("Received unknown interaction " + messageId);
            return;
        }

        String customId = event.getMessageComponentInteraction().getCustomId();
        switch (customId) {
            case "nextPageId" -> nextPage(event);
            case "randomPageId" -> randomPage(event);
            case "previousPageId" -> previousPage(event);
            case "addFavoriteId" -> addFavorite(event);
            case "deleteFavoriteId" -> removeFavorite(event);
            case "deleteMessageId" -> deleteMessage(event);
            default -> log.error("Incorrect custom id: " + customId);
        }
    }

    public void addPost(SlashCommandCreateEvent event, PostMessage postMessage) {
        if (event.getInteraction().getChannel().isEmpty()) {
            return;
        }

        PostMessageable postMessageable = postMessage.toPostMessageable();

        Message message = event.getSlashCommandInteraction().createImmediateResponder()
                .addEmbed(postMessageable.embed())
                .setContent(postMessageable.content())
                .addComponents(PostMessageButtons.actionRows(postMessage.getCurrentPost().isError()))
                .respond().join()
                .update().join();

        postMessageCache.addPost(message.getId(), postMessage);
    }

    /**
     * Updates interaction, and updates components
     */
    private void updatePostMessage(MessageComponentInteraction interaction, PostMessage postMessage) {
        PostMessageable postMessageable = postMessage.toPostMessageable();

        interaction.createOriginalMessageUpdater()
                .setContent(postMessageable.content())
                .addEmbed(postMessageable.embed())
                .addComponents(PostMessageButtons.actionRows(postMessage.getCurrentPost().isError()))
                .update().join();
    }

    /**
     * Update post message, not updating buttons if not available
     */
    private void updatePostMessage(PostMessage postMessage) {
        postMessageCache.findByPostMessageUuid(postMessage)
                .flatMap(discordApi::getCachedMessageById)
                .ifPresent(message -> {
                    PostMessageable postMessageable = postMessage.toPostMessageable();
                    message.edit(postMessageable.content(), postMessageable.embed());
                });
    }

    private void nextPage(MessageComponentCreateEvent event) {
        long messageId = event.getMessageComponentInteraction().getMessage().getId();
        PostMessage postMessage = postMessageCache.getPostMessageMap().get(messageId);

        postMessage.nextPage();
        updatePostMessage(event.getMessageComponentInteraction(), postMessage);
    }

    public void previousPage(MessageComponentCreateEvent event) {
        long messageId = event.getMessageComponentInteraction().getMessage().getId();
        PostMessage postMessage = postMessageCache.getPostMessageMap().get(messageId);

        postMessage.previousPage();
        updatePostMessage(event.getMessageComponentInteraction(), postMessage);
    }

    public void randomPage(MessageComponentCreateEvent event) {
        long messageId = event.getMessageComponentInteraction().getMessage().getId();
        PostMessage postMessage = postMessageCache.getPostMessageMap().get(messageId);
        postMessage.randomPage();

        updatePostMessage(event.getMessageComponentInteraction(), postMessage);
    }

    private void addFavorite(MessageComponentCreateEvent event) {
        long messageId = event.getMessageComponentInteraction().getMessage().getId();
        PostMessage postMessage = postMessageCache.getPostMessageMap().get(messageId);

        PostFetchResult postFetchResult = postMessage.getCurrentPost();

        String message;

        if (postFetchResult.isError()) {
            message = "Could not add favorite.";
        } else {
            User user = event.getInteraction().getUser();

            boolean added = favoritesService.addFavorite(user, postFetchResult.post());
            message = added ? "Successfully stored favorite." : "Already stored as favorite.";
        }

        event.getMessageComponentInteraction().createImmediateResponder()
                .setContent(message)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond().join();
    }

    private void removeFavorite(MessageComponentCreateEvent event) {
        long messageId = event.getMessageComponentInteraction().getMessage().getId();
        PostMessage postMessage = postMessageCache.getPostMessageMap().get(messageId);

        User reactingUser = event.getInteraction().getUser();

        PostFetchResult postFetchResult = postMessage.getCurrentPost();

        String message;

        if (postFetchResult.isError()) {
            message = "Could not remove favorite.";
        } else if (favoritesService.removeFavorite(reactingUser, postFetchResult.post())) {
            message = "Successfully removed favorite.";
        } else {
            message = "Not stored as favorite.";
        }

        event.getMessageComponentInteraction().createImmediateResponder()
                .setContent(message)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond().join();
    }

    private void deleteMessage(MessageComponentCreateEvent event) {
        Message message = event.getMessageComponentInteraction().getMessage();

        event.getMessageComponentInteraction().getMessage().delete().join();
        postMessageCache.getPostMessageMap().remove(message.getId());
    }

    @EventListener
    public void onApplicationEvent(FavoriteEvent favoriteEvent) {
        postMessageCache.getPostMessageMap().values().stream()
                .filter(FavoritesMessage.class::isInstance)
                .map(FavoritesMessage.class::cast)
                .filter(p -> p.getUser().getId() == favoriteEvent.getUser().getId())
                .forEach(p -> {
                    p.onFavoriteEvent(favoriteEvent);
                    updatePostMessage(p);
                });
    }

    @EventListener
    public void onApplicationEvent(HistoryEvent historyEvent) {
        postMessageCache.getPostMessageMap().values().stream()
                .filter(HistoryMessage.class::isInstance)
                .map(HistoryMessage.class::cast)
                .filter(historyMessage -> historyEvent.getChannel().getId() == historyMessage.getTextChannel().getId())
                .forEach(p -> {
                    p.onHistoryEvent(historyEvent);
                    updatePostMessage(p);
                });
    }

}
