package nsfw.post;

import nsfw.embed.PostEmbedOptions;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record PostMessageable(@NonNull String content, @Nullable EmbedBuilder embed) {

    public static PostMessageable fromPost(PostEmbedOptions options, EmbedBuilder embedBuilder) {
        if (options.getPost().isVideo()) {
            return new PostMessageable(options.getPost().getFileUrl(), null);
        }

        return new PostMessageable("", embedBuilder);
    }

    public static PostMessageable fromEmbed(EmbedBuilder embed) {
        return new PostMessageable("", embed);
    }
}
