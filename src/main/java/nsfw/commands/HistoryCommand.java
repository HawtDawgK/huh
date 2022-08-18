package nsfw.commands;

import lombok.RequiredArgsConstructor;
import nsfw.embed.EmbedService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandBuilder;
import nsfw.post.PostMessage;
import nsfw.post.PostMessageCache;
import nsfw.post.PostResolvableEntry;
import nsfw.post.history.HistoryMessage;
import nsfw.post.history.PostHistory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoryCommand implements Command {

    private final EmbedService embedService;

    private final PostMessageCache postMessageCache;

    private final PostHistory postHistory;

    private final ApplicationContext applicationContext;

    @Override
    public SlashCommandBuilder toSlashCommandBuilder() {
        return new SlashCommandBuilder()
                .setName("history")
                .setDescription("Shows post history");
    }

    @Override
    public void apply(SlashCommandCreateEvent event) {
        TextChannel messageChannel = event.getInteraction().getChannel().orElseThrow(() -> new IllegalArgumentException(""));
        List<PostResolvableEntry> postHistoryFromChannel = postHistory.getHistory(messageChannel);

        if (postHistoryFromChannel.isEmpty()) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(embedService.createErrorEmbed("No posts in history."))
                    .respond();
        }

        PostMessage postMessage = new HistoryMessage(applicationContext, postHistoryFromChannel, event);
        postMessage.initReply();

        postMessageCache.addPost(postMessage);
    }

}
