package launcher;

import api.ClientWrapper;
import commands.*;

import embed.ErrorEmbed;
import enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import post.PostMessages;
import post.api.PostFetchException;
import post.autocomplete.AutocompleteException;

import java.util.*;

@Slf4j
public class Launcher {

    private static final Map<String, Command> commandMap = new HashMap<>();

    static {
        commandMap.put("posts", new PostsCommand());
        commandMap.put("history", new HistoryCommand());
        commandMap.put("favorites", new FavoritesCommand());
    }

    public static void main(String[] args) {
        ClientWrapper.getApi().addSlashCommandCreateListener(Launcher::handleSlashCommand);
        ClientWrapper.getApi().addAutocompleteCreateListener(Launcher::handleAutocomplete);
        ClientWrapper.getApi().addMessageComponentCreateListener(PostMessages::handleInteraction);
    }

    private static void handleSlashCommand(SlashCommandCreateEvent event) {
        try {
            commandMap.get(event.getSlashCommandInteraction().getCommandName()).apply(event);
        } catch (CommandException| PostFetchException e) {
            log.error(e.getMessage(), e);
            event.getInteraction().createImmediateResponder()
                    .addEmbed(ErrorEmbed.create(e.getMessage()))
                    .respond();
        }
    }

    private static void handleAutocomplete(AutocompleteCreateEvent event) {
        try {
            AutocompleteInteraction interaction = event.getAutocompleteInteraction();
            Optional<String> optOption = interaction.getOptionStringValueByName("site");

            PostSite postSite = optOption.map(PostSite::findByName)
                    .orElseThrow(() -> new AutocompleteException("Invalid site"));

            if (postSite.getPostApi().hasAutocomplete()) {
                String enteredText = interaction.getOptionStringValueByName("tags").orElse("");

                List<SlashCommandOptionChoice> list = postSite.getPostApi().autocomplete(enteredText);
                event.getAutocompleteInteraction().respondWithChoices(list);
            } else {
                event.getAutocompleteInteraction().respondWithChoices(Collections.emptyList());
            }
        } catch (AutocompleteException e) {
            log.error(e.getMessage(), e);
            event.getAutocompleteInteraction().respondWithChoices(Collections.emptyList());
        }
    }
}
