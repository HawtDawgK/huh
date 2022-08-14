package nsfw.post.history;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import nsfw.post.PostListMessage;
import nsfw.post.PostResolvableEntry;
import org.springframework.context.ApplicationContext;

import java.util.List;

@Slf4j
public class HistoryMessage extends PostListMessage {

    public HistoryMessage(ApplicationContext applicationContext,
                          List<PostResolvableEntry> postList, SlashCommandCreateEvent event) {
        super(applicationContext, postList, event);
    }

    @Override
    public String getTitle() {
        return "Post history";
    }

    public synchronized void onHistoryEvent(HistoryEvent event) {
        TextChannel messageChannel = getMessage().getChannel();

        if (event.getChannel().equals(messageChannel)) {
            log.info("Received history event");

            getPostList().add(event.getNewEntry());
            editMessage();
        }
    }

}
