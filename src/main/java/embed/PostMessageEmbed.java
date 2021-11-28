package embed;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import post.Post;

public class PostMessageEmbed extends EmbedBuilder {

    public PostMessageEmbed(Post post) {
        setTitle("Post");
        setImage(post.getFileUrl());

        StringBuilder footerTextBuilder = new StringBuilder();

        post.getPostMetadata().ifPresent(metadata -> {
            footerTextBuilder.append("Page ");
            footerTextBuilder.append(metadata.getPage());
            footerTextBuilder.append(" of \u2022 ");
        });

        footerTextBuilder.append("Score: ").append(post.getScore());
        setFooter(footerTextBuilder.toString());

        setTimestamp(post.getCreatedAt().toInstant());
    }
}
