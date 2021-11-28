package embed;

import api.Api;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class ErrorEmbed extends EmbedBuilder {

    public ErrorEmbed() {
        User user = Api.getAPI().getYourself();

        setFooter(user.getName(), user.getAvatar());
        setColor(Color.RED);
        setTimestampToNow();
    }
}
