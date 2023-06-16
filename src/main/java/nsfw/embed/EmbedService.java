package nsfw.embed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.springframework.stereotype.Service;

import java.awt.Color;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbedService {

    private final DiscordApi discordApi;

    public EmbedBuilder createEmbed(PostEmbedOptions postEmbedOptions) {
        if (postEmbedOptions.getPostFetchResult().isError()) {
            return createErrorEmbed(postEmbedOptions.getPostFetchResult().message());
        }
        return createPostEmbed(postEmbedOptions);
    }

    public EmbedBuilder createPostEmbed(PostEmbedOptions options) {
        long displayPage = options.getPage() + 1;
        long count = options.getCount();
        long score = options.getPostFetchResult().post().getScore();

        String footer = "Page %d of %d • Score: %d".formatted(displayPage, count, score);

        return new EmbedBuilder()
                .setTitle(options.getTitle())
                .setDescription(options.getDescription())
                .setTimestamp(options.getPostFetchResult().post().getCreatedAt().toInstant())
                .setImage(options.getPostFetchResult().post().getFileUrl())
                .setFooter(footer);
    }

    public EmbedBuilder createErrorEmbed(String message) {
        User self = discordApi.getYourself();

        return new EmbedBuilder()
                .setTitle("Error")
                .setDescription(message)
                .setColor(Color.RED)
                .setTimestampToNow()
                .setFooter(self.getName(), self.getAvatar());
    }

}
