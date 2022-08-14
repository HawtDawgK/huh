package nsfw.post.history;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.channel.TextChannel;
import nsfw.post.PostResolvableEntry;

@Getter
@RequiredArgsConstructor
public class HistoryEvent {

    private final PostResolvableEntry newEntry;

    private final TextChannel channel;
}
