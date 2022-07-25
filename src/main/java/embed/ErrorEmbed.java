package embed;

import api.ClientWrapper;
import lombok.experimental.UtilityClass;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.time.Instant;

@UtilityClass
public class ErrorEmbed {

    public static EmbedBuilder create(String message) {
        EmbedBuilder embedCreateSpec = new EmbedBuilder()
                .setTitle("Error")
                .setDescription(message)
                .setColor(Color.RED)
                .setTimestamp(Instant.now());

        User self = ClientWrapper.getApi().getYourself();

        if (self != null) {
            embedCreateSpec.setFooter(self.getName(), self.getAvatar());
        }

        return embedCreateSpec;
    }
}
