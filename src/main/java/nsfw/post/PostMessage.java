package nsfw.post;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nsfw.embed.EmbedService;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import nsfw.post.api.PostFetchException;
import nsfw.post.favorites.FavoriteEvent;
import nsfw.post.favorites.FavoriteEventType;
import org.springframework.context.ApplicationContext;

import java.time.Instant;
import java.util.*;

@Slf4j
@Getter
@Setter
public abstract class PostMessage {

    private PostService postService;

    private EmbedService embedService;

    private PostMessageCache postMessageCache;

    private int page;

    private final SlashCommandCreateEvent event;

    private final Random random = new Random();
    private Message message;

    PostMessage(int page, SlashCommandCreateEvent event, ApplicationContext applicationContext) {
        this.page = page;
        this.event = event;
        this.postService = applicationContext.getBean(PostService.class);
        this.embedService = applicationContext.getBean(EmbedService.class);
        this.postMessageCache = applicationContext.getBean(PostMessageCache.class);
    }

    public abstract Post getCurrentPost() throws PostFetchException;

    abstract PostMessageable toPostMessageable();

    abstract int getCount();

    void nextPage() {
        page = Math.floorMod(page + 1, getCount());
    }

    void previousPage() {
        page = Math.floorMod(page - 1, getCount());
    }

    void randomPage() {
        page = random.nextInt(getCount());
    }

    public void updatePost(MessageComponentCreateEvent edit) {
        PostMessageable postMessageable = toPostMessageable();

        edit.getMessageComponentInteraction().createOriginalMessageUpdater()
                .setContent(postMessageable.getContent())
                .removeAllEmbeds()
                .addEmbed(postMessageable.getEmbed())
                .addComponents(getButtons().toArray(HighLevelComponent[]::new))
                .update().join();
    }

    private void addFavorite(MessageComponentCreateEvent event) {
        try {
            Post currentPost = getCurrentPost();

            PostResolvable currentResolvable = currentPost.toPostResolvable();
            User user = event.getInteraction().getUser();

            if (postService.hasFavorite(user, currentResolvable)) {
                event.getMessageComponentInteraction()
                        .createImmediateResponder()
                        .setContent("Already stored as favorite.")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                return;
            }

            postService.addFavorite(user, currentResolvable);

            PostResolvableEntry newEntry = new PostResolvableEntry(currentResolvable.getPostId(),
                    currentResolvable.getPostSite(), Instant.now());
            postMessageCache.onFavoriteEvent(new FavoriteEvent(user, newEntry, FavoriteEventType.ADDED));
            event.getMessageComponentInteraction()
                    .createImmediateResponder()
                    .setContent("Successfully stored favorite.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        } catch (PostFetchException e) {
            log.error(e.getMessage(), e);
            event.getMessageComponentInteraction()
                    .createImmediateResponder()
                    .addEmbed(embedService.createErrorEmbed("Error storing favorite"))
                    .respond();
        }
    }

    public void handleInteraction(MessageComponentCreateEvent event) {
        String customId = event.getMessageComponentInteraction().getCustomId();

        if (customId.equals("add-favorite")) {
            addFavorite(event);
            return;
        }
        if (customId.equals("delete-message")) {
            deleteMessage(event);
            return;
        }

        switch (customId) {
            case "next-page" -> nextPage();
            case "random-page" -> randomPage();
            case "previous-page" -> previousPage();
            default ->
                    log.warn("Received invalid interaction id " + event.getMessageComponentInteraction().getCustomId());
        }

        updatePost(event);
    }

    private void deleteMessage(MessageComponentCreateEvent buttonInteractionEvent) {
        User reactingUser = buttonInteractionEvent.getInteraction().getUser();
        User author = event.getInteraction().getUser();

        // Only author can delete the message
        if (reactingUser.equals(author)) {
            postMessageCache.removePost(this);
            buttonInteractionEvent.getMessageComponentInteraction().getMessage().delete().join();
        } else {
            buttonInteractionEvent.getMessageComponentInteraction()
                    .createImmediateResponder()
                    .setContent("Only the author can delete this message")
                    .respond();
        }
    }

    public List<HighLevelComponent> getButtons() {
        return PostMessageButtons.actionRow();
    }

    public void initReply() {
        PostMessageable postMessageable = toPostMessageable();

        this.message = event.getSlashCommandInteraction().createImmediateResponder()
                .setContent(postMessageable.getContent())
                .addEmbeds(postMessageable.getEmbed())
                .addComponents(getButtons().toArray(new HighLevelComponent[0]))
                .respond().join()
                .update().join();
    }
}
