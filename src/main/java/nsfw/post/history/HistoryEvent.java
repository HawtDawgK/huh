package nsfw.post.history;

import org.javacord.api.entity.channel.TextChannel;
import nsfw.post.PostResolvableEntry;
import org.springframework.context.ApplicationEvent;

public final class HistoryEvent extends ApplicationEvent {

    private final transient PostResolvableEntry newEntry;
    private final transient TextChannel channel;

    public HistoryEvent(PostResolvableEntry newEntry, TextChannel channel) {
        super(new Object());
        this.newEntry = newEntry;
        this.channel = channel;
    }

    public PostResolvableEntry getNewEntry() {
        return newEntry;
    }

    public TextChannel getChannel() {
        return channel;
    }

}
