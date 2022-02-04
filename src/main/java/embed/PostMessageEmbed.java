package embed;

import discord4j.core.spec.EmbedCreateFields.Footer;
import discord4j.core.spec.EmbedCreateSpec;
import post.Post;

public class PostMessageEmbed {

    private PostMessageEmbed() {

    }

    public static EmbedCreateSpec toEmbed(Post post, int page, int count) {
        String footerText = String.format("Page %d of %d \u2022 Score: %d", page, count, post.getScore());

        return EmbedCreateSpec.builder().title("Post")
                .image(post.getFileUrl())
                .timestamp(post.getCreatedAt().toInstant())
                .footer(Footer.of(footerText, null))
                .build();
    }

}
