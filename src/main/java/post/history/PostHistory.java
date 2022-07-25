package post.history;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.TextChannel;
import post.Post;
import post.PostMessages;
import post.PostResolvableEntry;
import util.LimitedSizeQueue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class PostHistory {

    private static final int MAX_LENGTH = 100;

    private static final HashMap<TextChannel, List<PostResolvableEntry>> POST_HISTORY = new HashMap<>();

    public static synchronized void addPost(TextChannel textChannel, Post post) {
        POST_HISTORY.putIfAbsent(textChannel, new LimitedSizeQueue<>(MAX_LENGTH));

        PostResolvableEntry postResolvableEntry = new PostResolvableEntry(post.getId(), post.getSite(), Instant.now());
        POST_HISTORY.get(textChannel).add(postResolvableEntry);

        HistoryEvent historyEvent = new HistoryEvent(postResolvableEntry, textChannel);
        PostMessages.onHistoryEvent(historyEvent);
    }

    public static List<PostResolvableEntry> getHistory(TextChannel textChannel) {
        List<PostResolvableEntry> currHistory = POST_HISTORY.getOrDefault(textChannel, new ArrayList<>());
        return new ArrayList<>(currHistory);
    }
}
