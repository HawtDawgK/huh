package nsfw.post.history;

import lombok.extern.slf4j.Slf4j;
import nsfw.db.PostEntity;
import nsfw.post.list.PostListMessage;
import org.javacord.api.entity.channel.TextChannel;

import java.util.List;

@Slf4j
public class HistoryMessage extends PostListMessage {

    private final TextChannel textChannel;

    public HistoryMessage(List<PostEntity> posts, TextChannel textChannel) {
        super(posts);
        this.textChannel = textChannel;
    }

    public String getTitle() {
        return "Post history";
    }

    @Override
    public String getErrorMessage() {
        return "No history";
    }

    public void onHistoryEvent(HistoryEvent event) {
        if (textChannel.equals(event.getChannel())) {
            getPosts().add(event.getNewEntry());
        }
    }

}
