package enums;

import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import post.api.PostApi;
import post.api.danbooru.DanbooruApi;
import post.api.rule34.Rule34Api;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PostSite {
    RULE34("rule34", new Rule34Api()),
    DANBOORU("danbooru", new DanbooruApi());

    private final String name;
    private final PostApi postApi;

    public ApplicationCommandOptionChoiceData toApplicationCommand() {
        return ApplicationCommandOptionChoiceData.builder()
                .name(name)
                .value(name)
                .build();
    }

    public static PostSite findByName(String name) {
        return Arrays.stream(values())
                .filter(v -> v.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Received invalid site " + name));
    }

}
