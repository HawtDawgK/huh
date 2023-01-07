package nsfw.post.history;

import org.javacord.api.entity.channel.TextChannel;
import nsfw.post.PostResolvableEntry;
import org.springframework.context.ApplicationEvent;

public final class HistoryEvent extends ApplicationEvent {

    private final PostResolvableEntry newEntry;
    private final TextChannel channel;

    public HistoryEvent(PostResolvableEntry newEntry, TextChannel channel) {
        super(new Object());
        this.newEntry = newEntry;
        this.channel = channel;
    }

    public PostResolvableEntry newEntry() {
        return newEntry;
    }

    public TextChannel channel() {
        return channel;
    }

    @Override
    public String toString() {
        return "HistoryEvent[" +
                "newEntry=" + newEntry + ", " +
                "channel=" + channel + ']';
    }

}
