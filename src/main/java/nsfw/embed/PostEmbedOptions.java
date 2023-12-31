package nsfw.embed;

import lombok.Builder;
import lombok.Getter;
import nsfw.post.PostFetchResult;
import org.springframework.lang.NonNull;

@Getter
@Builder
public class PostEmbedOptions {

    private final long page;

    private final long count;

    private final String title;

    private final String description;

    private final @NonNull PostFetchResult postFetchResult;
}
