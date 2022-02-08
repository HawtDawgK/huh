package post;

import db.PostRepository;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.User;
import discord4j.core.spec.*;
import embed.ErrorEmbed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import post.api.PostFetchException;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public abstract class PostMessage {

    private int page;

    private final ChatInputInteractionEvent event;

    private final Random random = new Random();

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

    void updatePost(ButtonInteractionEvent buttonInteractionEvent) {
        InteractionReplyEditMono edit = buttonInteractionEvent.editReply();
        edit = toPostMessageable(edit);

        buttonInteractionEvent.deferEdit().then(edit).block();
    }

    InteractionReplyEditMono toPostMessageable(InteractionReplyEditMono edit) {
        PostMessageable postMessageable = toPostMessageable();

        return edit.withContentOrNull(postMessageable.getContent())
                .withEmbeds(postMessageable.getEmbed())
                .withComponents(getButtons().toArray(LayoutComponent[]::new));
    }

    private Mono<Void> addFavorite(ButtonInteractionEvent event) {
        try {
            Optional<Post> optionalPost = getCurrentPost();

            if (optionalPost.isEmpty()) {
                return Mono.empty();
            }

            PostResolvableEntry currentEntry = optionalPost.get().toPostResolvableEntry();
            User user = event.getInteraction().getUser();

            if (PostRepository.hasFavorite(user, currentEntry)) {
                return event.reply("Already stored as favorite.").withEphemeral(true);
            }

            PostRepository.addFavorite(user, currentEntry);
            return event.reply("Successfully stored favorite.").withEphemeral(true);
        } catch (SQLException | PostFetchException e) {
            log.error(e.getMessage(), e);
            return event.reply().withEmbeds(ErrorEmbed.create("Error storing favorite."));
        }
    }

    public Mono<Void> handleInteraction(ButtonInteractionEvent buttonInteractionEvent) {
        String customId = buttonInteractionEvent.getCustomId();

        if (customId.equals("add-favorite")) {
            return addFavorite(buttonInteractionEvent);
        }
        if (customId.equals("delete-message")) {
            return deleteMessage();
        }

        switch (customId) {
            case "next-page" -> nextPage();
            case "random-page" -> randomPage();
            case "previous-page" -> previousPage();
            default -> log.warn("Received invalid interaction id " + buttonInteractionEvent.getCustomId());
        }

        updatePost(buttonInteractionEvent);
        return Mono.empty();
    }

    private Mono<Void> deleteMessage() {
        PostMessages.removePost(this);
        return event.deleteReply();
    }

    public List<LayoutComponent> getButtons() {
        return PostMessageButtons.actionRow();
    }

    public void initReply() {
        PostMessageable postMessageable = toPostMessageable();

        event.reply(postMessageable.getContent() != null ? postMessageable.getContent() : "")
                .withEmbeds(postMessageable.getEmbed())
                .withComponents(getButtons())
                .block();
    }
}
