package enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import post.api.PostApi;
import post.api.danbooru.DanbooruApi;
import post.api.gelbooru.GelbooruApi;
import post.api.hypnohub.HypnohubApi;
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
    TBIB("The Big Imageboard", new TbibApi()),
    HYPNOHUB("Hypnohub", new HypnohubApi());

    private final String name;
    private final PostApi postApi;

    public SlashCommandOptionChoice toApplicationCommand() {
        return SlashCommandOptionChoice.create(name, name);
    }

    public static PostSite findByName(String name) {
        return Arrays.stream(values())
                .filter(v -> v.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Received invalid site " + name));
    }

}
