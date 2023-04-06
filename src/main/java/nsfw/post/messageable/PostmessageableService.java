package nsfw.post.messageable;

import lombok.RequiredArgsConstructor;
import nsfw.embed.EmbedService;
import nsfw.embed.PostEmbedOptions;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostmessageableService {

    private final EmbedService embedService;

    public PostMessageable fromPost(PostEmbedOptions options) {
        EmbedBuilder embedBuilder = embedService.createEmbed(options);

        if (!options.getPostFetchResult().isError() && options.getPostFetchResult().post().isVideo()) {
            return new PostMessageable(options.getPostFetchResult().post().getFileUrl(), null);
        }

        return new PostMessageable("", embedBuilder);
    }

}
