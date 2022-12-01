package nsfw.post.autocomplete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.enums.PostSite;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutocompleteService {

    private final DiscordApi discordApi;

    @PostConstruct
    public void postConstruct() {
        discordApi.addAutocompleteCreateListener(this::autocomplete);
    }

    public void autocomplete(AutocompleteCreateEvent event) {
        AutocompleteInteraction interaction = event.getAutocompleteInteraction();
        try {
            PostSite postSite = interaction.getOptionByName("site")
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .map(PostSite::findByName)
                    .orElseThrow(() -> new AutocompleteException("Invalid site"));

            if (postSite.getPostApi().hasAutocomplete()) {
                String tag = interaction.getOptionByName("tags")
                        .flatMap(SlashCommandInteractionOption::getStringValue)
                        .orElse("");

                List<SlashCommandOptionChoice> choices = postSite.getPostApi().autocomplete(tag).stream()
                        .map(result -> SlashCommandOptionChoice.create(result.getLabel(), result.getValue()))
                        .toList();

                event.getAutocompleteInteraction().respondWithChoices(choices).join();
            } else {
                event.getAutocompleteInteraction().respondWithChoices(Collections.emptyList()).join();
            }
        } catch (AutocompleteException e) {
            log.error("Autocomplete error", e);
        }
    }
}
