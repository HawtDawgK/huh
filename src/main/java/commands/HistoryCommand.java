package commands;

import embed.ErrorEmbed;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandBuilder;
import post.PostMessage;
import post.PostMessages;
import post.PostResolvableEntry;
import post.history.HistoryMessage;
import post.history.PostHistory;

import java.util.List;

public class HistoryCommand implements Command {

    @Override
    public SlashCommandBuilder toSlashCommandBuilder() {
        return new SlashCommandBuilder()
                .setName("history")
                .setDescription("Shows post history");
    }

    @Override
    public void apply(SlashCommandCreateEvent event) throws CommandException {
        CommandUtil.checkNsfwChannel(event.getInteraction());

        TextChannel messageChannel = event.getInteraction().getChannel().orElseThrow(() -> new IllegalArgumentException(""));
        List<PostResolvableEntry> postHistoryFromChannel = PostHistory.getHistory(messageChannel);

        if (postHistoryFromChannel.isEmpty()) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(ErrorEmbed.create("No posts in history."))
                    .respond();
        }

        PostMessage postMessage = new HistoryMessage(postHistoryFromChannel, event);
        postMessage.initReply();

        PostMessages.addPost(postMessage);
    }

}
