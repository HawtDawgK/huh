package embed;

import discord4j.core.spec.EmbedCreateFields.Footer;
import discord4j.core.spec.EmbedCreateSpec;
import post.Post;

public class PostMessageEmbed {

    private PostMessageEmbed() {

    }

    public static EmbedCreateSpec toEmbed(Post post) {
        StringBuilder footerTextBuilder = new StringBuilder();

        post.getPostMetadata().ifPresent(metadata -> {
            footerTextBuilder.append("Page ");
            footerTextBuilder.append(metadata.getPage());
            footerTextBuilder.append(" of ");
            footerTextBuilder.append(metadata.getTagCount());
            footerTextBuilder.append(" \u2022 ");
        });

        footerTextBuilder.append("Score: ").append(post.getScore());

        return EmbedCreateSpec.builder().title("Post")
                .image(post.getFileUrl())
                .timestamp(post.getCreatedAt().toInstant())
                .footer(Footer.of(footerTextBuilder.toString(), null))
                .build();
    }

}
