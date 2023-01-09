package nsfw.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nsfw.post.api.PostApi;
import nsfw.post.api.danbooru.DanbooruApi;
import nsfw.post.api.gelbooru.GelbooruApi;
import nsfw.post.api.hypnohub.HypnohubApi;
import nsfw.post.api.rule34.Rule34Api;
import nsfw.post.api.tbib.TbibApi;
import nsfw.post.api.xbooru.XbooruApi;
import nsfw.post.api.yandere.YandereApi;
import org.javacord.api.interaction.SlashCommandOptionChoice;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PostSite {
    RULE34("Rule34", new Rule34Api()),
    DANBOORU("Danbooru", new DanbooruApi()),
    GELBOORU("Gelbooru", new GelbooruApi()),
    XBOORU("Xbooru", new XbooruApi()),
    TBIB("The Big Imageboard", new TbibApi()),
    HYPNOHUB("Hypnohub", new HypnohubApi()),
    YANDERE("Yande.re", new YandereApi());

    private final String name;
    private final PostApi postApi;

    public SlashCommandOptionChoice toSlashCommandOptionChoice() {
        return SlashCommandOptionChoice.create(name, name);
    }

    public static PostSite findByName(String name) {
        return Arrays.stream(values())
                .filter(v -> v.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Received invalid site " + name));
    }

}
