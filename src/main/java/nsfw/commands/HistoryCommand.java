package nsfw.commands;

import lombok.RequiredArgsConstructor;
import nsfw.db.PostEntity;
import nsfw.post.message.PostMessage;
import nsfw.post.PostService;
import nsfw.post.message.PostMessageService;
import nsfw.post.messageable.PostmessageableService;
import nsfw.post.history.HistoryMessage;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import nsfw.post.history.PostHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoryCommand implements Command {

    private final PostHistory postHistory;

    private final PostService postService;

    private final PostmessageableService postmessageableService;

    private final PostMessageService postMessageService;

    @Override
    public SlashCommandBuilder toSlashCommandBuilder() {
        return SlashCommand.with("history", "Shows post history per channel");
    }

    @Override
    public void apply(SlashCommandCreateEvent event) {
        TextChannel messageChannel = event.getInteraction().getChannel().
                orElseThrow(() -> new IllegalArgumentException(""));
        List<PostEntity> postHistoryFromChannel = postHistory.getHistory(messageChannel);

        PostMessage postMessage = new HistoryMessage(postService, postmessageableService,
                postHistoryFromChannel, messageChannel);
        postMessageService.addPost(event, postMessage);
    }

}
