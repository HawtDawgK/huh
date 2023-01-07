package nsfw.post.api;

import lombok.Builder;
import lombok.Getter;
import nsfw.enums.PostSite;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Builder
@Getter
public class PostFetchOptions {

    private @NonNull PostSite postSite;

    private @Nullable String tags;

    private @Nullable Long id;

    private @Nullable Long page;

    private boolean counts;
}
