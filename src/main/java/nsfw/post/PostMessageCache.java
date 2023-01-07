package nsfw.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.embed.EmbedService;
import nsfw.embed.PostEmbedOptions;
import nsfw.post.api.PostFetchException;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.favorites.FavoriteEvent;
import nsfw.post.favorites.FavoritesMessage;
import nsfw.post.favorites.FavoritesService;
import nsfw.post.history.HistoryEvent;
import nsfw.post.history.HistoryMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageCache {

    private final DiscordApi discordApi;

    private final FavoritesService favoritesService;

    private final EmbedService embedService;

    private final PostService postService;

    private static final Map<Long, PostMessage> postMessageMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void postConstruct() {
        discordApi.addMessageComponentCreateListener(this::handleInteraction);
    }

    public void addPost(SlashCommandCreateEvent event, PostMessage postMessage) {
        try {
            Post firstPost = postService.fetchPost(postMessage.getPostFetchOptions());
            postMessage.setCurrentPost(firstPost);
            PostMessageable postMessageable = toPostMessageable(postMessage);

            Message message = event.getSlashCommandInteraction().createImmediateResponder()
                    .setContent(postMessageable.content())
                    .addEmbeds(postMessageable.embed())
                    .addComponents(PostMessageButtons.PAGE_ROWS)
                    .respond().join()
                    .update().join();

            postMessageMap.put(message.getId(), postMessage);
        } catch (PostFetchException e) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .setContent("")
                    .removeAllEmbeds()
                    .addEmbed(embedService.createErrorEmbed(e.getMessage()))
                    .addComponents(PostMessageButtons.PAGE_ROWS)
                    .respond().join();
        }
    }

    public void handleInteraction(MessageComponentCreateEvent event) {
        long id = event.getMessageComponentInteraction().getMessage().getId();

        if (!postMessageMap.containsKey(id)) {
            log.info("Received unknown interaction");
            return;
        }

        String customId = event.getMessageComponentInteraction().getCustomId();

        if (customId.equals("add-favorite")) {
            addFavorite(event);
            return;
        }
        if (customId.equals("delete-message")) {
            deleteMessage(event);
            return;
        }
        if (customId.equals("delete-favorite")) {
            removeFavorite(event);
            return;
        }

        PostMessage postMessage = postMessageMap.get(id);

        switch (customId) {
            case "next-page" -> postMessage.nextPage();
            case "random-page" -> postMessage.randomPage();
            case "previous-page" -> postMessage.previousPage();
            default ->
                    log.warn("Received invalid interaction id " + event.getMessageComponentInteraction().getCustomId());
        }

        updatePost(event);
    }

    private void addFavorite(MessageComponentCreateEvent event) {
        long id = event.getMessageComponentInteraction().getMessage().getId();

        if (!postMessageMap.containsKey(id)) {
            log.info("Added favorite for unknown interaction");
            return;
        }

        PostMessage postMessage = postMessageMap.get(id);
        Post currentPost = postMessage.getCurrentPost();

        PostResolvable currentResolvable = currentPost.toPostResolvable(postMessage.getPostFetchOptions().getPostSite());
        User user = event.getInteraction().getUser();

        boolean added = favoritesService.addFavorite(user, currentResolvable);
        String message = added ? "Successfully stored favorite." : "Already stored as favorite.";

        event.getInteraction()
                .createImmediateResponder()
                .setContent(message)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond().join();
    }

    public void updatePost(MessageComponentCreateEvent edit) {
        long id = edit.getMessageComponentInteraction().getMessage().getId();

        if (!postMessageMap.containsKey(id)) {
            return;
        }

        PostMessage postMessage = postMessageMap.get(id);

        try {
            PostFetchOptions postFetchOptions = postMessage.getPostFetchOptions();
            Post post = postService.fetchPost(postFetchOptions);
            postMessage.setCurrentPost(post);

            PostMessageable postMessageable = toPostMessageable(postMessage);

            edit.getMessageComponentInteraction().createOriginalMessageUpdater()
                    .setContent(postMessageable.content())
                    .removeAllEmbeds()
                    .addEmbed(postMessageable.embed())
                    .addComponents(PostMessageButtons.PAGE_ROWS)
                    .update().join();
        } catch (PostFetchException e) {
            edit.getMessageComponentInteraction().createOriginalMessageUpdater()
                    .setContent("")
                    .removeAllEmbeds()
                    .addEmbed(embedService.createErrorEmbed(e.getMessage()))
                    .addComponents(PostMessageButtons.PAGE_ROWS)
                    .update().join();
        }
    }

    private void deleteMessage(MessageComponentCreateEvent messageComponentCreateEvent) {
        long id = messageComponentCreateEvent.getMessageComponentInteraction().getMessage().getId();

        if (!postMessageMap.containsKey(id)) {
            return;
        }

        User reactingUser = messageComponentCreateEvent.getInteraction().getUser();
        User author = messageComponentCreateEvent.getInteraction().getUser();

        // Only author can delete the message
        if (reactingUser.equals(author)) {
            postMessageMap.remove(id);
            messageComponentCreateEvent.getMessageComponentInteraction().getMessage().delete().join();
            postMessageMap.remove(id);
        } else {
            messageComponentCreateEvent.getMessageComponentInteraction()
                    .createImmediateResponder()
                    .setContent("Only the author can delete this message")
                    .respond().join();
        }
    }

    public synchronized void onHistoryEvent(HistoryEvent historyEvent) {
        postMessageMap.values().stream()
                .filter(HistoryMessage.class::isInstance)
                .map(HistoryMessage.class::cast)
                .forEach(p -> p.onHistoryEvent(historyEvent));
    }

    private PostMessageable toPostMessageable(PostMessage postMessage) {
        Post currentPost = postMessage.getCurrentPost();

        PostEmbedOptions postEmbedOptions = PostEmbedOptions.builder()
                .post(currentPost)
                .page(postMessage.getPage())
                .count(postMessage.getCount())
                .build();

        return PostMessageable.fromPost(postEmbedOptions, embedService);
    }

    private void removeFavorite(MessageComponentCreateEvent event) {
        long id = event.getMessageComponentInteraction().getMessage().getId();

        if (!postMessageMap.containsKey(id)) {
            return;
        }

        PostMessage postMessage = postMessageMap.get(id);
        User reactingUser = event.getInteraction().getUser();

        PostResolvable postResolvableEntry = postMessage.getCurrentPost()
                .toPostResolvable(postMessage.getPostFetchOptions().getPostSite());
        boolean removed = favoritesService.removeFavorite(reactingUser, postResolvableEntry);

        String message = removed ? "Successfully removed favorite." : "Not stored as favorite.";

        event.getMessageComponentInteraction().createImmediateResponder()
                .setContent(message)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond().join();
    }

    @EventListener
    public void onApplicationEvent(FavoriteEvent event) {
        postMessageMap.values().stream()
                .filter(FavoritesMessage.class::isInstance)
                .map(FavoritesMessage.class::cast)
                .forEach(p -> p.onFavoriteEvent(event));
    }
}
