package nsfw.post.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.PostMessage;
import nsfw.post.PostResolvableEntry;
import nsfw.post.api.PostFetchOptions;
import org.javacord.api.entity.channel.TextChannel;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HistoryMessage extends PostMessage {

    private final List<PostResolvableEntry> posts;

    private final TextChannel textChannel;

    public String getTitle() {
        return "Post history";
    }

    public void onHistoryEvent(HistoryEvent event) {
        if (textChannel.equals(event.getChannel())) {
            posts.add(event.getNewEntry());
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
