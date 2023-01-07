package nsfw.embed;

import lombok.Builder;
import lombok.Getter;
import nsfw.post.Post;
import nsfw.post.PostResolvableEntry;
import org.springframework.lang.NonNull;

@Getter
@Builder
public class PostEmbedOptions {

    private final int page;

    private final int count;

    private final String title;

    private final String description;

    private final PostResolvableEntry entry;

    private final @NonNull Post post;
}
