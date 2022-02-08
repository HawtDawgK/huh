package post.autocomplete;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutocompleteResult {

    private String label;

    private String value;

    public ApplicationCommandOptionChoiceData toApplicationCommandOptionChoiceData(String originalInput) {
        return ApplicationCommandOptionChoiceData.builder()
                .name(originalInput + label)
                .value(originalInput + value)
                .build();
    }
}
