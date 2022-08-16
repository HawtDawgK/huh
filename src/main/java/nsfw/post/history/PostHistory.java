package nsfw.post.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.Post;
import nsfw.post.PostMessages;
import nsfw.util.LimitedSizeQueue;
import org.javacord.api.entity.channel.TextChannel;
import nsfw.post.PostResolvableEntry;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostHistory {

    private final PostMessages postMessages;

    private static final int MAX_LENGTH = 100;

    private final HashMap<TextChannel, List<PostResolvableEntry>> history = new HashMap<>();

    public synchronized void addPost(TextChannel textChannel, Post post) {
        history.putIfAbsent(textChannel, new LimitedSizeQueue<>(MAX_LENGTH));

        PostResolvableEntry postResolvableEntry = new PostResolvableEntry(post.getId(), post.getSite(), Instant.now());
        history.get(textChannel).add(postResolvableEntry);

        HistoryEvent historyEvent = new HistoryEvent(postResolvableEntry, textChannel);
        postMessages.onHistoryEvent(historyEvent);
    }

    public List<PostResolvableEntry> getHistory(TextChannel textChannel) {
        List<PostResolvableEntry> currHistory = history.getOrDefault(textChannel, new ArrayList<>());

        // Done to create unmodifiable copy
        return new ArrayList<>(currHistory);
    }
}
