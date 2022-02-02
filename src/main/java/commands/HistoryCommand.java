package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.ApplicationCommandRequest;
import embed.ErrorEmbed;
import post.PostMessageFactory;
import post.PostResolvable;
import post.history.PostHistory;
import reactor.core.publisher.Mono;

import java.util.List;

public class HistoryCommand implements Command {

    @Override
    public ApplicationCommandRequest toApplicationCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name("history")
                .description("Shows post history")
                .build();
    }

    @Override
    public Mono<Void> apply(ChatInputInteractionEvent event) throws CommandException {
        CommandUtil.checkNsfwChannel(event);

        MessageChannel messageChannel = event.getInteraction().getChannel().block();
        List<PostResolvable> postHistoryFromChannel = PostHistory.getHistory(messageChannel);

        if (postHistoryFromChannel.isEmpty()) {
            return event.reply().withEmbeds(ErrorEmbed.create("No posts in history"));
        }

        return PostMessageFactory.createPostListMessage(postHistoryFromChannel, event);
    }

}
