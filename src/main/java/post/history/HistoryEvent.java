package post.history;

import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import post.PostResolvableEntry;

@Getter
@RequiredArgsConstructor
public class HistoryEvent {

    private final PostResolvableEntry newEntry;

    private final MessageChannel channel;
}
