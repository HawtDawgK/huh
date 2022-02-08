package post;

import discord4j.core.spec.EmbedCreateSpec;
import embed.PostEmbedOptions;
import embed.PostMessageEmbed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public class PostMessageable {

    private final @Nullable String content;
    private final @Nullable EmbedCreateSpec embed;

    public static PostMessageable fromPost(PostEmbedOptions options) {
        if (options.getPost().isAnimated()) {
            return new PostMessageable(options.getPost().getFileUrl(), null);
        }

        return new PostMessageable(null, PostMessageEmbed.fromPost(options));
    }

    public static PostMessageable fromEmbed(EmbedCreateSpec embed) {
        return new PostMessageable(null, embed);
    }
}
