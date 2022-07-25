package post.api.hypnohub;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.javacord.api.interaction.SlashCommandOptionChoice;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HypnohubTag {

    private int id;

    private String name;

    private int count;

    private int type;

    private boolean ambiguous;

    public SlashCommandOptionChoice toApplicationCommandOptionChoiceData() {
        return SlashCommandOptionChoice.create(name, name);
    }
}
