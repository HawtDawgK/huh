package embed;

import lombok.experimental.UtilityClass;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@UtilityClass
public class PostMessageEmbed {

    public static EmbedBuilder fromPost(PostEmbedOptions options) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (options.getTitle() != null) {
            embedBuilder.setTitle(options.getTitle());
        }
        if (options.getDescription() != null) {
            embedBuilder.setDescription(options.getDescription());
        }

        if (options.getEntry() != null) {
            embedBuilder.setTimestamp(options.getEntry().getStoredAt());
        } else {
            embedBuilder.setTimestamp(options.getPost().getCreatedAt().toInstant());
        }

        embedBuilder.setImage(options.getPost().getFileUrl());
        embedBuilder.setFooter(toFooter(options.getPage(), options.getCount(), options.getPost().getScore()));

        return embedBuilder;
    }

    private static String toFooter(int page, int count, long score) {
        return String.format("Page %d of %d \u2022 Score: %d", page + 1, count, score);
    }

}
