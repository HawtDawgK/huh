package post;

import discord4j.core.spec.EmbedCreateSpec;
import embed.PostMessageEmbed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public class PostMessageable {

    private final @Nullable String content;
    private final @Nullable EmbedCreateSpec embed;

    public static PostMessageable fromListPost(Post post, int page, int count,
                                               String title, String description,
                                               PostResolvableEntry entry) {
        if (post.isAnimated()) {
            return new PostMessageable(post.getFileUrl(), null);
        }

        return new PostMessageable(null, PostMessageEmbed.fromListPost(post, entry, page, count, title, description));
    }

    public static PostMessageable fromPost(Post post, int page, int count) {
        if (post.isAnimated()) {
            return new PostMessageable(post.getFileUrl(), null);
        }

        return new PostMessageable(null, PostMessageEmbed.fromPost(post, page, count));
    }

    public static PostMessageable fromEmbed(EmbedCreateSpec embed) {
        return new PostMessageable(null, embed);
    }
}
