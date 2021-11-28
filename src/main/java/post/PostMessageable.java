package post;

import embed.PostMessageEmbed;
import lombok.Getter;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jetbrains.annotations.Nullable;

@Getter
public class PostMessageable {

    private final @Nullable String content;
    private final @Nullable EmbedBuilder embed;

    public PostMessageable(@Nullable String content, @Nullable EmbedBuilder embed) {
        this.content = content;
        this.embed = embed;
    }

    public static PostMessageable fromPost(Post post) {
        if (post.isAnimated()) {
            return new PostMessageable(post.getFileUrl(), null);
        }

        return new PostMessageable(null, new PostMessageEmbed(post));
    }

}
