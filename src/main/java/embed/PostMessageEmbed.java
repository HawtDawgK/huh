package embed;

import discord4j.core.spec.EmbedCreateFields.Footer;
import discord4j.core.spec.EmbedCreateSpec;
import org.jetbrains.annotations.Nullable;
import post.Post;

import java.time.Instant;

public class PostMessageEmbed {

    private PostMessageEmbed() {

    }

    public static EmbedCreateSpec toEmbed(Post post, int page, int count, @Nullable Instant instant) {
        String footerText = String.format("Page %d of %d \u2022 Score: %d", page, count, post.getScore());

        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder().title("Post")
                .image(post.getFileUrl())
                .footer(Footer.of(footerText, null));

        if (instant != null) {
            builder.timestamp(instant);
        } else {
            builder.timestamp(post.getCreatedAt().toInstant());
        }

        return builder.build();
    }

}
