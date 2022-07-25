package embed;

import api.ClientWrapper;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.time.Instant;

@Slf4j
public class PostNotFoundEmbed {

    private PostNotFoundEmbed() { }

    public static EmbedBuilder create(String tags) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("No posts found")
                .setDescription("No posts found for " + tags)
                .setColor(Color.RED)
                .setTimestamp(Instant.now());

        User self = ClientWrapper.getApi().getYourself();

        if (self == null) {
            log.warn("Could not fetch self data");
        } else {
            embedBuilder.setFooter(self.getName(), self.getAvatar());
        }

        return embedBuilder;
    }
}
