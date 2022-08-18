package nsfw.post.autocomplete;

import nsfw.enums.PostSite;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutocompleteService {

    public void autocomplete(AutocompleteCreateEvent event) throws AutocompleteException {
        AutocompleteInteraction interaction = event.getAutocompleteInteraction();
        Optional<String> optOption = interaction.getOptionStringValueByName("site");

        PostSite postSite = optOption.map(PostSite::findByName)
                .orElseThrow(() -> new AutocompleteException("Invalid site"));

        if (postSite.getPostApi().hasAutocomplete()) {
            String tag = interaction.getOptionStringValueByName("tags").orElse("");
            List<AutocompleteResult> autocompleteResults = postSite.getPostApi().autocomplete(tag);

            List<SlashCommandOptionChoice> choices = autocompleteResults.stream()
                    .map(result -> SlashCommandOptionChoice.create(result.getLabel(), result.getValue()))
                    .collect(Collectors.toList());

            event.getAutocompleteInteraction().respondWithChoices(choices).join();
        } else {
            event.getAutocompleteInteraction().respondWithChoices(Collections.emptyList()).join();
        }
    }
}
