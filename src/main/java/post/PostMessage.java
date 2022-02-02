package post;

import db.PostRepository;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.User;
import discord4j.core.spec.*;
import embed.ErrorEmbed;
import embed.PostNotFoundEmbed;
import enums.PostSite;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import post.api.PostApi;
import post.api.PostFetchException;
import post.history.PostHistory;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Getter
public class PostMessage {

    private int page;
    private final int count;
    private final String tags;
    private final ChatInputInteractionEvent event;
    private final PostSite postSite;
    private final PostApi postApi;

    private final Random random = new Random();

    public PostMessage(int count, String tags, ChatInputInteractionEvent event, PostSite postSite) {
        this.tags = tags;
        this.event = event;
        this.postSite = postSite;
        this.postApi = postSite.getPostApi();
        this.count = Math.min(count, postApi.getMaxCount());
    }

    void nextPage() {
        page = (page + 1) % count;
    }

    void previousPage() {
        page = (page - 1) % count;
    }

    void randomPage() {
        page = random.nextInt(count + 1);
    }

    void updatePost(ButtonInteractionEvent buttonInteractionEvent) {
        try {
            Optional<Post> optionalPost = getCurrentPost();

            InteractionReplyEditMono edit = buttonInteractionEvent.editReply();
            edit = toPostMessageable(edit, optionalPost);

            buttonInteractionEvent.deferEdit().then(edit).block();
        } catch (PostFetchException e) {
            buttonInteractionEvent.editReply().withEmbeds(ErrorEmbed.create("Error occured while fetching post"));
        }
    }

    Optional<Post> getCurrentPost() throws PostFetchException {
        Optional<Post> optionalPost = postApi.fetchByTagsAndPage(tags, page);

        optionalPost.ifPresent(post -> PostHistory.addPost(event.getInteraction().getChannel().block(), post, postApi));
        return optionalPost;
    }

    PostMessageable toPostMessageable(Optional<Post> optionalPost) {
        return optionalPost.map(PostMessageable::fromPost)
                .orElseGet(() -> PostMessageable.fromEmbed(PostNotFoundEmbed.create(tags)));
    }

    InteractionReplyEditMono toPostMessageable(InteractionReplyEditMono edit, Optional<Post> optionalPost) {
        PostMessageable postMessageable = toPostMessageable(optionalPost);

        return edit.withContentOrNull(postMessageable.getContent())
                .withEmbeds(postMessageable.getEmbed())
                .withComponents(PostMessageButtons.actionRow());
    }

    private Mono<Void> addFavorite(ButtonInteractionEvent event) {
        User user = event.getInteraction().getUser();

        try {
            if (PostRepository.hasFavorite(user.getId().asLong(), getCurrentPost().get().getId(), postSite)) {
                return event.reply("Already stored as favorite").withEphemeral(true);
            } else {
                PostRepository.addFavorite(user.getId().asLong(), getCurrentPost().get().getId(), postSite);
                return event.reply("Successfully stored favorite").withEphemeral(true);
            }
        } catch (PostFetchException e) {
            log.error(e.getMessage());
            return Mono.empty();
        } catch (SQLException e) {
            return event.reply().withEmbeds(ErrorEmbed.create("Could not store favorite"));
        }
    }

    public Mono<Void> handleInteraction(ButtonInteractionEvent buttonInteractionEvent) {
        switch (buttonInteractionEvent.getCustomId()) {
            case "next-page" -> nextPage();
            case "random-page" -> randomPage();
            case "previous-page" -> previousPage();
            case "add-favorite" -> {
                return addFavorite(buttonInteractionEvent);
            }
            case "delete-message" -> {
                event.deleteReply().block();
                PostMessages.removePost(this);
                return Mono.empty();
            }
            default -> log.warn("Received invalid interaction id " + buttonInteractionEvent.getCustomId());
        }

        updatePost(buttonInteractionEvent);
        return Mono.empty();
    }

    public void initReply() {
        try {
            Optional<Post> currentPost = getCurrentPost();
            PostMessageable postMessageable = PostMessageable.fromOptionalPost(currentPost, tags);

            event.reply(postMessageable.getContent() != null ? postMessageable.getContent() : "")
                    .withEmbeds(postMessageable.getEmbed())
                    .withComponents(PostMessageButtons.actionRow())
                    .block();
        } catch (PostFetchException e) {
            event.reply()
                    .withEmbeds(ErrorEmbed.create(e.getMessage()))
                    .withComponents(PostMessageButtons.actionRow())
                    .block();
        }
    }
}
