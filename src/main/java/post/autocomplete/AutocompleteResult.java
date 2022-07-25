package post.autocomplete;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionChoiceBuilder;

import java.util.StringJoiner;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutocompleteResult {

    private String label;

    private String value;

    public SlashCommandOptionChoice toApplicationCommandOptionChoiceData(String originalInput) {
        String[] splitInput = originalInput.split(" ");
        String[] splitInputWithoutLast = new String[splitInput.length - 1];

        System.arraycopy(splitInput, 0, splitInputWithoutLast, 0, splitInput.length - 1);

        StringJoiner stringJoiner = new StringJoiner(" ");

        for (String part : splitInputWithoutLast) {
            stringJoiner.add(part);
        }

        String originalInputWithoutLastWord = stringJoiner.toString();

        return new SlashCommandOptionChoiceBuilder()
                .setName(originalInputWithoutLastWord + " " + value)
                .setValue(originalInputWithoutLastWord + " " + value)
                .build();
    }
}
