package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import post.PostMessageFactory;
import reactor.core.publisher.Mono;

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
        return PostMessageFactory.createListPost(event);
    }

}
