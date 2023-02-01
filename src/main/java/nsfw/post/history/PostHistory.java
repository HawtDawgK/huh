package nsfw.post.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.db.PostEntity;
import nsfw.util.LimitedSizeQueue;
import org.javacord.api.entity.channel.TextChannel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostHistory {

    private static final int MAX_LENGTH = 100;

    private final HashMap<TextChannel, List<PostEntity>> history = new HashMap<>();

    @EventListener
    public void onApplicationEvent(HistoryEvent historyEvent) {
        history.putIfAbsent(historyEvent.getChannel(), new LimitedSizeQueue<>(MAX_LENGTH));
        history.get(historyEvent.getChannel()).add(historyEvent.getNewEntry());
    }

    public List<PostEntity> getHistory(TextChannel textChannel) {
        List<PostEntity> currHistory = history.getOrDefault(textChannel, new ArrayList<>());
        return new ArrayList<>(currHistory);
    }
}
