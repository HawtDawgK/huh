package nsfw.post.messageable;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record PostMessageable(@NonNull String content, @Nullable EmbedBuilder embed) {


}
