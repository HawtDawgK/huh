package embed;

import api.ClientWrapper;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields.Footer;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class PostNotFoundEmbed {

    private PostNotFoundEmbed() { }

    public static EmbedCreateSpec create(String tags) {
        EmbedCreateSpec.Builder embedCreateSpec = EmbedCreateSpec.builder()
                .title("No posts found")
                .description("No posts found for " + tags)
                .color(Color.RED)
                .timestamp(Instant.now());

        User self = ClientWrapper.getClient().getSelf().block();

        if (self != null) {
            embedCreateSpec.footer(Footer.of(self.getUsername(), self.getAvatarUrl()));
        } else {
            log.warn("Could not fetch self data");
        }

        return embedCreateSpec.build();
    }
}
