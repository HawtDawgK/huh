package nsfw.post.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import nsfw.post.PostMessage;
import nsfw.post.PostResolvableEntry;
import nsfw.post.api.PostFetchOptions;
import org.javacord.api.entity.channel.TextChannel;

import java.util.List;

@Log
@RequiredArgsConstructor
public class HistoryMessage extends PostMessage {

    private final List<PostResolvableEntry> posts;

    private final TextChannel textChannel;

    public String getTitle() {
        return "Post history";
    }

    public synchronized void onHistoryEvent(HistoryEvent event) {
        if (textChannel.equals(event.channel())) {
            log.info("Received history event");
            posts.add(event.newEntry());
        }
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public PostFetchOptions getPostFetchOptions() {
        PostResolvableEntry currentEntry = posts.get(getPage());

        return PostFetchOptions.builder()
                .postSite(currentEntry.getPostSite())
                .id(currentEntry.getPostId())
                .build();
    }
}
