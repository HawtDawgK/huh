package nsfw.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.embed.EmbedService;
import nsfw.embed.PostEmbedOptions;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.favorites.FavoriteEvent;
import nsfw.post.favorites.FavoritesMessage;
import nsfw.post.favorites.FavoritesService;
import nsfw.post.history.HistoryEvent;
import nsfw.post.history.HistoryMessage;
import nsfw.post.list.PostListMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMessageCache {

    private final DiscordApi discordApi;

    private final FavoritesService favoritesService;

    private final EmbedService embedService;

    private final PostService postService;

    private static final Map<String, DiscordReactionData> postMessageMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void postConstruct() {
        discordApi.addMessageComponentCreateListener(this::handleInteraction);
    }

    public void addPost(SlashCommandCreateEvent event, PostMessage postMessage) {
        if (event.getInteraction().getChannel().isEmpty()) {
            return;
        }

        String nextPageId = UUID.randomUUID().toString();
        String randomPageId = UUID.randomUUID().toString();
        String previousPageId = UUID.randomUUID().toString();
        String addFavoriteId = UUID.randomUUID().toString();
        String deleteMessageId = UUID.randomUUID().toString();
        String deleteFavoriteId = UUID.randomUUID().toString();

        List<ActionRow> actionRows = PostMessageButtons.actionRows(nextPageId, randomPageId, previousPageId,
                addFavoriteId, deleteMessageId, deleteFavoriteId);

        postMessageMap.put(nextPageId, new DiscordReactionData(postMessage, DiscordReactionType.NEXT_PAGE, actionRows));
        postMessageMap.put(randomPageId, new DiscordReactionData(postMessage, DiscordReactionType.RANDOM_PAGE, actionRows));
        postMessageMap.put(previousPageId, new DiscordReactionData(postMessage, DiscordReactionType.PREVIOUS_PAGE, actionRows));
        postMessageMap.put(addFavoriteId, new DiscordReactionData(postMessage, DiscordReactionType.ADD_FAVORITE, actionRows));
        postMessageMap.put(deleteMessageId, new DiscordReactionData(postMessage, DiscordReactionType.DELETE_MESSAGE, actionRows));
        postMessageMap.put(deleteFavoriteId, new DiscordReactionData(postMessage, DiscordReactionType.DELETE_FAVORITE, actionRows));

        updatePost(new DiscordReactionData(postMessage, null, actionRows), event.getInteraction());
    }

    private void nextPage(MessageComponentCreateEvent event) {
        String customId = event.getMessageComponentInteraction().getCustomId();
        DiscordReactionData discordReactionData = postMessageMap.get(customId);

        discordReactionData.postMessage().nextPage();
        updatePost(discordReactionData, event.getInteraction());
    }

    public void previousPage(MessageComponentCreateEvent event) {
        String customId = event.getMessageComponentInteraction().getCustomId();
        DiscordReactionData discordReactionData = postMessageMap.get(customId);

        discordReactionData.postMessage().previousPage();
        updatePost(discordReactionData, event.getInteraction());
    }

    public void randomPage(MessageComponentCreateEvent event) {
        String customId = event.getMessageComponentInteraction().getCustomId();
        DiscordReactionData discordReactionData = postMessageMap.get(customId);
        discordReactionData.postMessage().randomPage();

        updatePost(discordReactionData, event.getInteraction());
    }

    public void handleInteraction(MessageComponentCreateEvent event) {
        String customId = event.getMessageComponentInteraction().getCustomId();

        DiscordReactionData discordReactionData = postMessageMap.get(customId);

        if (discordReactionData == null) {
            log.error("Received unknown interaction " + customId);
            return;
        }

        switch (discordReactionData.actionType()) {
            case NEXT_PAGE -> nextPage(event);
            case RANDOM_PAGE -> randomPage(event);
            case PREVIOUS_PAGE -> previousPage(event);
            case ADD_FAVORITE -> addFavorite(event);
            case DELETE_FAVORITE -> removeFavorite(event);
            case DELETE_MESSAGE -> deleteMessage(event);
            default -> log.error("Incorrect action type: " + discordReactionData.actionType());
        }
    }

    private void addFavorite(MessageComponentCreateEvent event) {
        PostMessage postMessage = postMessageMap.get(event.getMessageComponentInteraction().getCustomId())
                .postMessage();

        Post currentPost = postMessage.getCurrentPost();

        User user = event.getInteraction().getUser();

        boolean added = favoritesService.addFavorite(user, currentPost);
        String message = added ? "Successfully stored favorite." : "Already stored as favorite.";

        event.getMessageComponentInteraction()
                .createImmediateResponder()
                .setContent(message)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond().join();
    }

    private void removeFavorite(MessageComponentCreateEvent event) {
        PostMessage postMessage = postMessageMap.get(event.getMessageComponentInteraction().getCustomId()).postMessage();

        User reactingUser = event.getInteraction().getUser();

        boolean removed = favoritesService.removeFavorite(reactingUser, postMessage.getCurrentPost());

        String message = removed ? "Successfully removed favorite." : "Not stored as favorite.";

        event.getMessageComponentInteraction().createImmediateResponder()
                .setContent(message)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond().join();
    }

    private void deleteMessage(MessageComponentCreateEvent event) {
        String customId = event.getMessageComponentInteraction().getCustomId();

        User reactingUser = event.getInteraction().getUser();
        User author = event.getInteraction().getUser();

        // Only author can delete the message
        if (reactingUser.equals(author)) {
            event.getMessageComponentInteraction().getMessage().delete().join();

            PostMessage postMessage = postMessageMap.get(customId).postMessage();

            postMessageMap.entrySet().stream()
                    .filter(entry -> entry.getValue().postMessage().getUuid().equals(postMessage.getUuid()))
                    .map(Map.Entry::getKey)
                    .forEach(postMessageMap::remove);
        } else {
            event.getMessageComponentInteraction()
                    .createImmediateResponder()
                    .setContent("Only the author can delete this message")
                    .respond().join();
        }
    }

    public void updatePost(DiscordReactionData discordReactionData, InteractionBase interactionBase) {
        PostMessage postMessage = discordReactionData.postMessage();

        PostFetchOptions postFetchOptions = postMessage.getPostFetchOptions();
        PostFetchResult postFetchResult = postService.fetchPost(interactionBase.getChannel().orElse(null), postFetchOptions);

        PostEmbedOptions postEmbedOptions = PostEmbedOptions.builder()
                .post(postMessage.getCurrentPost())
                .title(postMessage.getTitle())
                .page(postMessage.getPage())
                .count(postMessage.getCount())
                .post(postFetchResult.post())
                .build();

        EmbedBuilder postEmbed = embedService.createPostEmbed(postEmbedOptions);
        PostMessageable postMessageable = PostMessageable.fromPost(postEmbedOptions, postEmbed);

        InteractionImmediateResponseBuilder immediateResponder = interactionBase.createImmediateResponder();
        immediateResponder.removeAllEmbeds();
        immediateResponder.addComponents(discordReactionData.actionRowArray());
        immediateResponder.setContent(postMessageable.content());

        if (postFetchResult.isError() ) {
            immediateResponder.addEmbed(embedService.createErrorEmbed(""));
        } else if (postMessage instanceof PostListMessage listMessage && listMessage.getPosts().isEmpty()) {
            immediateResponder.addEmbed(embedService.createErrorEmbed("No posts present."));
        } else {
            postMessage.setCurrentPost(postFetchResult.post());
            immediateResponder.addEmbed(embedService.createPostEmbed(postEmbedOptions));
        }

        immediateResponder.respond().join();
    }

    @EventListener
    public void onApplicationEvent(FavoriteEvent favoriteEvent) {
        postMessageMap.values().stream()
                .filter(p -> p.postMessage() instanceof FavoritesMessage)
                .map(FavoritesMessage.class::cast)
                .forEach(p -> p.onFavoriteEvent(favoriteEvent));
    }

    @EventListener
    public void onApplicationEvent(HistoryEvent historyEvent) {
        postMessageMap.values().stream()
                .filter(p -> p.postMessage() instanceof HistoryMessage)
                .map(HistoryMessage.class::cast)
                .forEach(p -> p.onHistoryEvent(historyEvent));
    }
}
