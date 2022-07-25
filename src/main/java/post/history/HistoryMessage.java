package post.history;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import post.PostListMessage;
import post.PostResolvableEntry;

import java.util.List;

@Slf4j
public class HistoryMessage extends PostListMessage {

    public HistoryMessage(List<PostResolvableEntry> postList, SlashCommandCreateEvent event) {
        super(postList, event);
    }

    @Override
    public String getTitle() {
        return "Post history";
    }

    public synchronized void onHistoryEvent(HistoryEvent event) {
        TextChannel messageChannel = getEvent().getInteraction().getChannel().get();

        if (event.getChannel().equals(messageChannel)) {
            log.info("Received history event");

            getPostList().add(event.getNewEntry());
            editMessage();
        }
    }

}
