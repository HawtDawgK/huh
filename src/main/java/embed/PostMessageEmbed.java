package embed;

import discord4j.core.spec.EmbedCreateFields.Footer;
import discord4j.core.spec.EmbedCreateSpec;
import post.Post;
import post.PostResolvableEntry;

public class PostMessageEmbed {

    private PostMessageEmbed() {

    }

    public static EmbedCreateSpec fromListPost(Post post, PostResolvableEntry entry,
                                               int page, int count, String title, String description) {

        return EmbedCreateSpec.builder().title("Post")
                .title(title)
                .description(description)
                .image(post.getFileUrl())
                .footer(toFooter(page, count, post.getScore()))
                .timestamp(entry.getStoredAt())
                .build();
    }

    public static EmbedCreateSpec fromPost(Post post, int page, int count) {
        return EmbedCreateSpec.builder().title("Post")
                .image(post.getFileUrl())
                .footer(toFooter(page, count, post.getScore()))
                .timestamp(post.getCreatedAt().toInstant())
                .build();
    }

    private static Footer toFooter(int page, int count, long score) {
        return Footer.of(String.format("Page %d of %d \u2022 Score: %d", page + 1, count, score), null);
    }

}
