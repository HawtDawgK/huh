package embed;

import discord4j.core.spec.EmbedCreateFields.Footer;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PostMessageEmbed {

    public static EmbedCreateSpec fromPost(PostEmbedOptions options) {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();

        if (options.getTitle() != null) {
            embedBuilder.title(options.getTitle());
        }
        if (options.getDescription() != null) {
            embedBuilder.description(options.getDescription());
        }

        if (options.getEntry() != null) {
            embedBuilder.timestamp(options.getEntry().getStoredAt());
        } else {
            embedBuilder.timestamp(options.getPost().getCreatedAt().toInstant());
        }

        embedBuilder.image(options.getPost().getFileUrl());
        embedBuilder.footer(toFooter(options.getPage(), options.getCount(), options.getPost().getScore()));

        return embedBuilder.build();
    }

    private static Footer toFooter(int page, int count, long score) {
        return Footer.of(String.format("Page %d of %d \u2022 Score: %d", page + 1, count, score), null);
    }

}
