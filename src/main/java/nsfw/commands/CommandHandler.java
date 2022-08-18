package nsfw.commands;

import lombok.RequiredArgsConstructor;
import nsfw.api.ClientWrapper;
import nsfw.embed.EmbedService;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.autocomplete.AutocompleteService;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.springframework.stereotype.Component;
import nsfw.post.PostMessageCache;
import nsfw.post.api.PostFetchException;
import nsfw.post.autocomplete.AutocompleteException;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {

    private static final Map<String, Command> commandMap = new HashMap<>();

    private final ClientWrapper clientWrapper;

    private final CommandUtil commandUtil;

    private final EmbedService embedService;

    private final PostsCommand postsCommand;

    private final HistoryCommand historyCommand;

    private final FavoritesCommand favoritesCommand;

    private final PostMessageCache postMessageCache;

    private final AutocompleteService autocompleteService;

    @PostConstruct
    public void init() {
        commandMap.put("posts", postsCommand);
        commandMap.put("history", historyCommand);
        commandMap.put("favorites", favoritesCommand);

        commandMap.values().forEach(commandUtil::createCommand);

        clientWrapper.getApi().addSlashCommandCreateListener(this::handleSlashCommand);
        clientWrapper.getApi().addAutocompleteCreateListener(this::handleAutocomplete);
        clientWrapper.getApi().addMessageComponentCreateListener(postMessageCache::handleInteraction);
    }

    private void handleSlashCommand(SlashCommandCreateEvent event) {
        try {
            CommandUtil.checkNsfwChannel(event.getSlashCommandInteraction());
            commandMap.get(event.getSlashCommandInteraction().getCommandName()).apply(event);
        } catch (CommandException | PostFetchException e) {
            log.error(e.getMessage(), e);
            event.getInteraction().createImmediateResponder()
                    .addEmbed(embedService.createErrorEmbed(e.getMessage()))
                    .respond();
        }
    }

    private void handleAutocomplete(AutocompleteCreateEvent event) {
        try {
            autocompleteService.autocomplete(event);
        } catch (AutocompleteException e) {
            log.error(e.getMessage(), e);
            event.getAutocompleteInteraction().respondWithChoices(Collections.emptyList());
        }
    }
}
