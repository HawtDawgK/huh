package nsfw.embed;

import lombok.Builder;
import lombok.Getter;
import nsfw.db.PostEntity;
import nsfw.post.Post;
import org.springframework.lang.NonNull;

@Getter
@Builder
public class PostEmbedOptions {

    private final int page;

    private final int count;

    private final String title;

    private final String description;

    private final PostEntity postEntity;

    private final @NonNull Post post;
}
