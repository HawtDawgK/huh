package nsfw.post.autocomplete;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.enums.PostSite;
import nsfw.post.api.PostApi;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutocompleteService {

    private final DiscordApi discordApi;

    private final ObjectMapper objectMapper;

    private final WebClient webClient;

    @PostConstruct
    public void postConstruct() {
        discordApi.addAutocompleteCreateListener(this::autocomplete);
    }

    public void autocomplete(AutocompleteCreateEvent event) {
        try {
            AutocompleteInteraction interaction = event.getAutocompleteInteraction();

            PostSite postSite = interaction.getOptionByName("site")
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .map(PostSite::findByName)
                    .orElseThrow(() -> new AutocompleteException("Invalid site"));

            String tags = interaction.getOptionByName("tags")
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElse("");

            PostApi postApi = postSite.getPostApi();

            String responseBody = webClient.get()
                    .uri(postApi.getAutocompleteUrl(tags))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            CollectionLikeType collectionLikeType = TypeFactory.defaultInstance()
                    .constructCollectionLikeType(List.class, postApi.getAutocompleteResultType());

            List<AutocompleteResult> autocompleteResults = objectMapper.readValue(responseBody, collectionLikeType);

            List<SlashCommandOptionChoice> choices = autocompleteResults.stream()
                    .map(result -> SlashCommandOptionChoice.create(result.getLabel(), result.getValue()))
                    .toList();
            event.getAutocompleteInteraction().respondWithChoices(choices).join();
        } catch (AutocompleteException | JsonProcessingException e) {
            log.error("Error during autocomplete ", e);
            event.getAutocompleteInteraction().respondWithChoices(Collections.emptyList()).join();
        }
    }
}
