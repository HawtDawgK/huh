package post.history;

import discord4j.core.object.entity.channel.MessageChannel;
import post.Post;
import post.PostResolvable;
import util.LimitedSizeQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostHistory {

    private static final int MAX_LENGTH = 100;

    private static final HashMap<MessageChannel, List<PostResolvable>> POST_HISTORY = new HashMap<>();

    public static void addPost(MessageChannel messageChannel, Post post) {
        POST_HISTORY.putIfAbsent(messageChannel, new LimitedSizeQueue<>(MAX_LENGTH));
        POST_HISTORY.get(messageChannel).add(new PostResolvable(post.getId(), post.getSite()));
    }

    public static List<PostResolvable> getHistory(MessageChannel messageChannel) {
        return POST_HISTORY.getOrDefault(messageChannel, new ArrayList<>());
    }
}
