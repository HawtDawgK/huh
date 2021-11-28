package enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import post.api.PostApi;
import post.rule34.Rule34Api;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PostSite {
    RULE34("rule34", new Rule34Api());

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
