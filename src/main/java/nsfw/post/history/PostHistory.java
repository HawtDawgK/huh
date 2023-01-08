package nsfw.post.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.PostResolvable;
import nsfw.post.PostResolvableEntry;
import nsfw.util.LimitedSizeQueue;
import org.javacord.api.entity.channel.TextChannel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostHistory {

    private static final int MAX_LENGTH = 100;

    private final HashMap<TextChannel, List<PostResolvableEntry>> history = new HashMap<>();

    @EventListener
    public void onApplicationEvent(HistoryEvent historyEvent) {
        List<PostResolvableEntry> postResolvableEntries = history.get(historyEvent.getChannel());

        if (postResolvableEntries != null) {
            postResolvableEntries.add(historyEvent.getNewEntry());
        }
    }

    public synchronized void addPost(TextChannel textChannel, PostResolvable postResolvable) {
        history.putIfAbsent(textChannel, new LimitedSizeQueue<>(MAX_LENGTH));

        PostResolvableEntry postResolvableEntry = new PostResolvableEntry(postResolvable.getPostId(),
                postResolvable.getPostSite(), Instant.now());
        history.get(textChannel).add(postResolvableEntry);
    }

    public List<PostResolvableEntry> getHistory(TextChannel textChannel) {
        List<PostResolvableEntry> currHistory = history.getOrDefault(textChannel, new ArrayList<>());

        // Done to create unmodifiable copy
        return new ArrayList<>(currHistory);
    }
}
