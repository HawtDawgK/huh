package embed;

import api.ClientWrapper;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.experimental.UtilityClass;

import java.time.Instant;

@UtilityClass
public class ErrorEmbed {

    public static EmbedCreateSpec create(String message) {
        EmbedCreateSpec.Builder embedCreateSpec = EmbedCreateSpec.builder()
                .title("Error")
                .description(message)
                .color(Color.RED)
                .timestamp(Instant.now());

        User self = ClientWrapper.getClient().getSelf().block();

        if (self != null) {
            embedCreateSpec.footer(self.getUsername(), self.getAvatarUrl());
        }

        return embedCreateSpec.build();
    }
}
