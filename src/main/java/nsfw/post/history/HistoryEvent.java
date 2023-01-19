package nsfw.post.history;

import nsfw.db.PostEntity;
import org.javacord.api.entity.channel.TextChannel;
import org.springframework.context.ApplicationEvent;

public final class HistoryEvent extends ApplicationEvent {

    private final transient PostEntity newEntry;
    private final transient TextChannel channel;

    public HistoryEvent(PostEntity newEntry, TextChannel channel) {
        super(new Object());
        this.newEntry = newEntry;
        this.channel = channel;
    }

    public PostEntity getNewEntry() {
        return newEntry;
    }

    public TextChannel getChannel() {
        return channel;
    }

}
