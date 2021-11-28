package post;

import api.Api;
import embed.PostNotFoundEmbed;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import post.api.PostApi;

import java.util.Optional;

@Log4j2
@Getter
public class PostMessage {

    private int page;
    private final int count;
    private final String tags;
    private final Message message;
    private final PostApi postApi;
    private final MessageComponentCreateListener messageComponentCreateListener = this::handleInteraction;

    public PostMessage(int count, String tags, Message message, PostApi postApi) {
        this.count = Math.min(count, postApi.getMaxCount());
        this.tags = tags;
        this.message = message;
        this.postApi = postApi;
    }

    public void registerListener() {
        Api.getAPI().addMessageComponentCreateListener(messageComponentCreateListener);
    }

    void nextPage() {
        page = (page + 1) % count;
        updatePost();
    }

    void previousPage() {
        page = (page - 1) % count;
        updatePost();
    }

    void randomPage() {
        page = (int) (Math.random() * (count + 1));
        updatePost();
    }

    void deleteMessage() {
        message.delete().join();
        Api.getAPI().removeListener(this.messageComponentCreateListener);
    }

    private void updatePost() {
        Optional<Post> optionalPost = postApi.fetchByTagsAndPage(tags, page);

        optionalPost.ifPresentOrElse(post -> {
            PostMessageable postMessageable = PostMessageable.fromPost(post);
            message.edit(postMessageable.getContent(), postMessageable.getEmbed()).join();
        }, () -> message.edit(new PostNotFoundEmbed(tags)).join());
    }

    public void handleInteraction(MessageComponentCreateEvent messageComponentCreateEvent) {
        MessageComponentInteraction interaction = messageComponentCreateEvent.getMessageComponentInteraction();

        switch (interaction.getCustomId()) {
            case "next-page" -> nextPage();
            case "random-page" -> randomPage();
            case "previous-page" -> previousPage();
            case "add-favorite" -> log.warn("Not implemented");
            case "delete-message" -> {
                deleteMessage();
                return;
            }
            default -> log.warn("Invalid case for interaction id " + interaction.getCustomId());
        }

        interaction.createOriginalMessageUpdater()
                .addComponents(PostMessageFactory.createActionRow())
                .update()
                .join();
    }
}
