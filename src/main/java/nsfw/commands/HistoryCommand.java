package nsfw.commands;

import lombok.RequiredArgsConstructor;
import nsfw.embed.EmbedService;
import nsfw.post.PostMessage;
import nsfw.post.PostMessageCache;
import nsfw.post.history.HistoryMessage;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import nsfw.post.PostResolvableEntry;
import nsfw.post.history.PostHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoryCommand implements Command {

    private final EmbedService embedService;

    private final PostHistory postHistory;

    private final PostMessageCache postMessageCache;

    @Override
    public SlashCommandBuilder toSlashCommandBuilder() {
        return SlashCommand.with("history", "Shows post history per channel");
    }

    @Override
    public void apply(SlashCommandCreateEvent event) {
        TextChannel messageChannel = event.getInteraction().getChannel().
                orElseThrow(() -> new IllegalArgumentException(""));
        List<PostResolvableEntry> postHistoryFromChannel = postHistory.getHistory(messageChannel);

        if (postHistoryFromChannel.isEmpty()) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(embedService.createErrorEmbed("No posts in history."))
                    .respond().join();
        }

        PostMessage postMessage = new HistoryMessage(postHistoryFromChannel, messageChannel);
        postMessageCache.addPost(event, postMessage);
    }

}
