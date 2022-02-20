package post.history;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.extern.slf4j.Slf4j;
import post.PostListMessage;
import post.PostResolvableEntry;

import java.util.List;

@Slf4j
public class HistoryMessage extends PostListMessage {

    public HistoryMessage(List<PostResolvableEntry> postList, ChatInputInteractionEvent event) {
        super(postList, event);
    }

    public synchronized void onHistoryEvent(HistoryEvent event) {
        MessageChannel messageChannel = getEvent().getInteraction().getChannel().block();

        if (!event.getChannel().equals(messageChannel)) {
            return;
        }
        log.info("Received history event");

        getPostList().add(event.getNewEntry());
        editMessage();
    }

}
