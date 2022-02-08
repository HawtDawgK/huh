package post.history;

import discord4j.core.object.entity.channel.MessageChannel;
import post.Post;
import post.PostResolvableEntry;
import util.LimitedSizeQueue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostHistory {

    private static final int MAX_LENGTH = 100;

    private static final HashMap<MessageChannel, List<PostResolvableEntry>> POST_HISTORY = new HashMap<>();

    public static void addPost(MessageChannel messageChannel, Post post) {
        POST_HISTORY.putIfAbsent(messageChannel, new LimitedSizeQueue<>(MAX_LENGTH));

        PostResolvableEntry postResolvableEntry = new PostResolvableEntry(post.getId(), post.getSite(), Instant.now());
        POST_HISTORY.get(messageChannel).add(postResolvableEntry);
    }

    public static List<PostResolvableEntry> getHistory(MessageChannel messageChannel) {
        return POST_HISTORY.getOrDefault(messageChannel, new ArrayList<>());
    }
}
