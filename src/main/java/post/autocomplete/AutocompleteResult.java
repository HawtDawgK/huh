package post.autocomplete;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.Getter;

import java.util.StringJoiner;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutocompleteResult {

    private String label;

    private String value;

    public ApplicationCommandOptionChoiceData toApplicationCommandOptionChoiceData(String originalInput) {
        String[] splitInput = originalInput.split(" ");
        String[] splitInputWithoutLast = new String[splitInput.length - 1];

        System.arraycopy(splitInput, 0, splitInputWithoutLast, 0, splitInput.length - 1);

        StringJoiner stringJoiner = new StringJoiner(" ");

        for (String part : splitInputWithoutLast) {
            stringJoiner.add(part);
        }

        String originalInputWithoutLastWord = stringJoiner.toString();

        return ApplicationCommandOptionChoiceData.builder()
                .name(originalInputWithoutLastWord + " " + value)
                .value(originalInputWithoutLastWord + " " + value)
                .build();
    }
}
