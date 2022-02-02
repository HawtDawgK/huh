package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import post.api.PostFetchException;
import reactor.core.publisher.Mono;

public interface Command {

    ApplicationCommandRequest toApplicationCommandRequest();

    Mono<Void> apply(ChatInputInteractionEvent event) throws CommandException, PostFetchException;
}
