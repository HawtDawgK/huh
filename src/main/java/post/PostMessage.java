package post;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.*;
import embed.PostNotFoundEmbed;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import post.api.PostApi;
import post.history.PostHistory;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Getter
public class PostMessage {

    private int page;
    private final int count;
    private final String tags;
    private final ChatInputInteractionEvent event;
    private final PostApi postApi;

    private final Random random = new Random();

    public PostMessage(int count, String tags, ChatInputInteractionEvent event, PostApi postApi) {
        this.count = Math.min(count, postApi.getMaxCount());
        this.tags = tags;
        this.event = event;
        this.postApi = postApi;
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
        Optional<Post> optionalPost = getCurrentPost();

        InteractionReplyEditMono edit = buttonInteractionEvent.editReply();
        edit = toPostMessageable(edit, optionalPost);

        buttonInteractionEvent.deferEdit().then(edit).block();
    }

    Optional<Post> getCurrentPost() {
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

    public Mono<Void> handleInteraction(ButtonInteractionEvent buttonInteractionEvent) {
        switch (buttonInteractionEvent.getCustomId()) {
            case "next-page" -> nextPage();
            case "random-page" -> randomPage();
            case "previous-page" -> previousPage();
            case "add-favorite" -> log.warn("Not implemented");
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
        Optional<Post> currentPost = getCurrentPost();
        PostMessageable postMessageable = PostMessageable.fromOptionalPost(currentPost, tags);

        event.reply(postMessageable.getContent() != null ? postMessageable.getContent() : "")
                .withEmbeds(postMessageable.getEmbed())
                .withComponents(PostMessageButtons.actionRow())
                .block();
    }
}
