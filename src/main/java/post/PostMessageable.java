package post;

import discord4j.core.spec.EmbedCreateSpec;
import embed.PostMessageEmbed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class PostMessageable {

    private final @Nullable String content;
    private final @Nullable EmbedCreateSpec embed;

    public static PostMessageable fromPost(Post post, int page, int count, @Nullable Instant instant) {
        if (post.isAnimated()) {
            return new PostMessageable(post.getFileUrl(), null);
        }

        return new PostMessageable(null, PostMessageEmbed.toEmbed(post, page, count, instant));
    }

    public static PostMessageable fromEmbed(EmbedCreateSpec embed) {
        return new PostMessageable(null, embed);
    }
}
