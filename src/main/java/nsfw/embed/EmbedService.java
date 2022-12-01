package nsfw.embed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbedService {

    private final DiscordApi discordApi;

    public EmbedBuilder createPostEmbed(PostEmbedOptions options) {
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

        String footer = String.format("Page %d of %d \u2022 Score: %d", options.getPage() + 1, options.getCount(),
                options.getPost().getScore());
        embedBuilder.setFooter(footer);

        return embedBuilder;
    }

    public EmbedBuilder createErrorEmbed(String message) {
        EmbedBuilder embedCreateSpec = new EmbedBuilder()
                .setTitle("Error")
                .setDescription(message)
                .setColor(Color.RED)
                .setTimestamp(Instant.now());

        User self = discordApi.getYourself();
        embedCreateSpec.setFooter(self.getName(), self.getAvatar());

        return embedCreateSpec;
    }

    public EmbedBuilder createNoPostsFoundEmbed(String tags) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("No posts found")
                .setDescription("No posts found for " + tags)
                .setColor(Color.RED)
                .setTimestamp(Instant.now());

        User self = discordApi.getYourself();
        embedBuilder.setFooter(self.getName(), self.getAvatar());

        return embedBuilder;
    }
}
