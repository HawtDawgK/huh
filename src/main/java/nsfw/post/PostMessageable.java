package nsfw.post;

import lombok.NonNull;
import nsfw.embed.EmbedService;
import nsfw.embed.PostEmbedOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@RequiredArgsConstructor
public class PostMessageable {

    private final @NonNull String content;
    private final @Nullable EmbedBuilder embed;

    public static PostMessageable fromPost(PostEmbedOptions options, EmbedService embedService) {
        if (options.getPost().isVideo()) {
            return new PostMessageable(options.getPost().getFileUrl(), null);
        }

        return new PostMessageable("", embedService.createPostEmbed(options));
    }

    public static PostMessageable fromEmbed(EmbedBuilder embed) {
        return new PostMessageable("", embed);
    }
}
