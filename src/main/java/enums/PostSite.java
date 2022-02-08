package enums;

import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import post.api.PostApi;
import post.api.danbooru.DanbooruApi;
import post.api.gelbooru.GelbooruApi;
import post.api.rule34.Rule34Api;
import post.api.tbib.TbibApi;
import post.api.xbooru.XbooruApi;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PostSite {
    RULE34("Rule34", new Rule34Api()),
    DANBOORU("Danbooru", new DanbooruApi()),
    GELBOORU("Gelbooru", new GelbooruApi()),
    XBOORU("Xbooru", new XbooruApi()),
    TBIB("The Big Imageboard", new TbibApi());

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
