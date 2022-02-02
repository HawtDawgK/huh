package launcher;

import api.ClientWrapper;
import commands.*;
import db.PostRepository;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
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

    private static final Map<String, Command> commandMap = createMap();

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
            return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(ErrorEmbed.create(e.getMessage()))
                    .build());
        }
    }

    private static Mono<Void> handleAutocomplete(ChatInputAutoCompleteEvent event) {
        PostSite postSite = PostSite.findByName(event.getOption("site")
                .map(ApplicationCommandInteractionOption::getValue)
                .flatMap(x -> x.map(ApplicationCommandInteractionOptionValue::asString))
                .orElse(PostSite.RULE34.getName()));

        try {
            if (postSite.getPostApi().hasAutocomplete()) {
                List<ApplicationCommandOptionChoiceData> list = postSite.getPostApi().autocomplete(
                        event.getFocusedOption().getValue().map(ApplicationCommandInteractionOptionValue::asString).orElse(""));
                return event.respondWithSuggestions(list);
            }

            return Mono.empty();
        } catch (AutocompleteException e) {
            return Mono.empty();
        }
    }

    private static HashMap<String, Command> createMap() {
        HashMap<String, Command> tempCommandHashMap = new HashMap<>();
        tempCommandHashMap.put("posts", new PostsCommand());
        tempCommandHashMap.put("history", new HistoryCommand());
        tempCommandHashMap.put("favorites", new FavoritesCommand());
        tempCommandHashMap.values().forEach(CommandUtil::createCommand);

        return tempCommandHashMap;
    }
}
