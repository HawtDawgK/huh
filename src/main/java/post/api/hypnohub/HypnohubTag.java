package post.api.hypnohub;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HypnohubTag {

    private int id;

    private String name;

    private int count;

    private int type;

    private boolean ambiguous;

    public ApplicationCommandOptionChoiceData toApplicationCommandOptionChoiceData() {
        return ApplicationCommandOptionChoiceData.builder()
                .name(name)
                .value(name)
                .build();
    }
}
