package launcher;

import api.ClientWrapper;
import commands.*;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import embed.ErrorEmbed;
import enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import post.PostMessages;
import post.api.PostFetchException;
import post.autocomplete.AutocompleteException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Launcher {

    private static final Map<String, Command> commandMap = new HashMap<>();

    static {
        commandMap.put("posts", new PostsCommand());
        commandMap.put("history", new HistoryCommand());
        commandMap.put("favorites", new FavoritesCommand());
        commandMap.values().forEach(CommandUtil::createCommand);
    }

    public static void main(String[] args) {
        Flux<Void> ready = ClientWrapper.getClient().on(ReadyEvent.class, event -> {
            PostMessages.setListeners();
            log.info("Bot is ready");
            return Mono.empty();
        });

        Flux<Void> slashCommand = ClientWrapper.getClient().on(ChatInputInteractionEvent.class, Launcher::handleSlashCommand);
        Flux<Void> autocomplete = ClientWrapper.getClient().on(ChatInputAutoCompleteEvent.class, Launcher::handleAutocomplete);

        Mono.when(ready, slashCommand, autocomplete).block();
    }

    private static Mono<Void> handleSlashCommand(ChatInputInteractionEvent event) {
        try {
            return commandMap.get(event.getCommandName()).apply(event);
        } catch (CommandException| PostFetchException e) {
            log.error(e.getMessage(), e);
            return event.reply().withEmbeds(ErrorEmbed.create(e.getMessage()));
        }
    }

    private static Mono<Void> handleAutocomplete(ChatInputAutoCompleteEvent event) {
        try {
            PostSite postSite = PostSite.findByName(event.getOption("site")
                    .map(ApplicationCommandInteractionOption::getValue)
                    .flatMap(x -> x.map(ApplicationCommandInteractionOptionValue::asString))
                    .orElseThrow(() -> new AutocompleteException("Invalid site")));

            if (postSite.getPostApi().hasAutocomplete()) {
                List<ApplicationCommandOptionChoiceData> list = postSite.getPostApi().autocomplete(
                        event.getFocusedOption().getValue().map(ApplicationCommandInteractionOptionValue::asString).orElse(""));
                return event.respondWithSuggestions(list);
            }

            return Mono.empty();
        } catch (AutocompleteException e) {
            log.error(e.getMessage(), e);
            return Mono.empty();
        }
    }
}
