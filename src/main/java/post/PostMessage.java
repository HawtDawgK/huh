package post;

import db.PostRepository;
import embed.ErrorEmbed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import post.api.PostFetchException;
import post.favorites.FavoriteEvent;
import post.favorites.FavoriteEventType;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public abstract class PostMessage {

    private int page;

    private final SlashCommandCreateEvent event;

    private final Random random = new Random();
    private Message message;

    public abstract Optional<Post> getCurrentPost() throws PostFetchException;

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

        List<EmbedBuilder> embedCreateSpecs = new ArrayList<>();
        if (postMessageable.getEmbed() != null) {
            embedCreateSpecs.add(postMessageable.getEmbed());
        }

        edit.getMessageComponentInteraction().createOriginalMessageUpdater()
                .setContent(postMessageable.getContent() != null ? postMessageable.getContent() : "")
                .removeAllEmbeds()
                .addEmbeds(embedCreateSpecs)
                .addComponents(getButtons().toArray(HighLevelComponent[]::new))
                .update()
                .join();
    }

    private void addFavorite(MessageComponentCreateEvent event) {
        try {
            Optional<Post> optionalPost = getCurrentPost();

            if (optionalPost.isEmpty()) {
                event.getMessageComponentInteraction()
                        .createImmediateResponder()
                        .setContent("Error fetching favorite")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                return;
            }

            PostResolvable currentResolvable = optionalPost.get().toPostResolvable();
            User user = event.getInteraction().getUser();

            if (PostRepository.hasFavorite(user, currentResolvable)) {
                event.getMessageComponentInteraction()
                        .createImmediateResponder()
                        .setContent("Already stored as favorite.")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                return;
            }

            PostRepository.addFavorite(user, currentResolvable);

            PostResolvableEntry newEntry = new PostResolvableEntry(currentResolvable.getPostId(),
                    currentResolvable.getPostSite(), Instant.now());
            PostMessages.onFavoriteEvent(new FavoriteEvent(user, newEntry, FavoriteEventType.ADDED));
            event.getMessageComponentInteraction()
                    .createImmediateResponder()
                    .setContent("Successfully stored favorite.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        } catch (SQLException | PostFetchException e) {
            log.error(e.getMessage(), e);
            event.getMessageComponentInteraction()
                    .createImmediateResponder()
                    .addEmbed(ErrorEmbed.create("Error storing favorite."))
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
        if (!reactingUser.equals(author)) {
            buttonInteractionEvent
                    .getMessageComponentInteraction()
                    .createImmediateResponder()
                    .setContent("Only the author can delete this message")
                    .respond();
        } else {
            PostMessages.removePost(this);
            buttonInteractionEvent.getMessageComponentInteraction().getMessage().delete().join();
        }
    }

    public List<HighLevelComponent> getButtons() {
        return PostMessageButtons.actionRow();
    }

    public void initReply() {
        PostMessageable postMessageable = toPostMessageable();
        List<EmbedBuilder> embedCreateSpecs = new ArrayList<>();

        if (postMessageable.getEmbed() != null) {
            embedCreateSpecs.add(postMessageable.getEmbed());
        }

        this.message = event.getSlashCommandInteraction().createImmediateResponder()
                .setContent(postMessageable.getContent() != null ? postMessageable.getContent() : "")
                .addEmbeds(embedCreateSpecs)
                .addComponents(getButtons().toArray(new HighLevelComponent[0]))
                .respond()
                .join()
                .update().join();
    }
}
