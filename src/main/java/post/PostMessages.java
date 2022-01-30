package post;

import api.ClientWrapper;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class PostMessages {

    private static final Map<Snowflake, PostMessage> postMessageMap = new HashMap<>();

    public static void addPost(PostMessage postMessage) {
        Message message = postMessage.getEvent().getReply().block();

        if (message != null) {
            postMessageMap.put(message.getId(), postMessage);
        } else {
            log.warn("Could not fetch reply for " + postMessage);
        }
    }

    public static void removePost(PostMessage postMessage) {
        postMessageMap.remove(postMessage.getEvent().getInteraction().getId(), postMessage);
    }

    public static void setListeners() {
        ClientWrapper.getClient()
                .on(ButtonInteractionEvent.class, PostMessages::handleInteraction)
                .subscribe();
    }

    public static Mono<Void> handleInteraction(ButtonInteractionEvent buttonInteractionEvent) {
        Optional<Message> optionalMessage = buttonInteractionEvent.getMessage();

        if (optionalMessage.isEmpty()) {
            log.warn("Received buttonInteractionEvent without message");
            return Mono.empty();
        }

        Message message = optionalMessage.get();
        if (!postMessageMap.containsKey(message.getId())) {
            log.warn("Received buttonInteractionEvent for unknown message id {}", message.getId());
            return Mono.empty();
        }

        PostMessage postMessage = postMessageMap.get(message.getId());
        return postMessage.handleInteraction(buttonInteractionEvent);
    }
}
