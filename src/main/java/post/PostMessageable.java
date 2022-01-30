package post;

import discord4j.core.spec.EmbedCreateSpec;
import embed.PostMessageEmbed;
import embed.PostNotFoundEmbed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class PostMessageable {

    private final @Nullable String content;
    private final @Nullable EmbedCreateSpec embed;

    public static PostMessageable fromOptionalPost(Optional<Post> optionalPost, String tags) {
        return optionalPost.map(PostMessageable::fromPost)
                .orElseGet(() ->fromEmbed(PostNotFoundEmbed.create(tags)));
    }

    public static PostMessageable fromPost(Post post) {
        if (post.isAnimated()) {
            return new PostMessageable(post.getFileUrl(), null);
        }

        return new PostMessageable(null, PostMessageEmbed.toEmbed(post));
    }

    public static PostMessageable fromEmbed(EmbedCreateSpec embed) {
        return new PostMessageable(null, embed);
    }
}
