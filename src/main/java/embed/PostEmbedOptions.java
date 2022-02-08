package embed;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import post.Post;
import post.PostResolvableEntry;

@Getter
@Builder
public class PostEmbedOptions {

    private final int page;

    private final int count;

    private final String title;

    private final String description;

    private final PostResolvableEntry entry;

    @NonNull
    private final Post post;
}
