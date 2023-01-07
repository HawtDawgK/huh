package nsfw.commands;

import lombok.RequiredArgsConstructor;
import nsfw.enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import nsfw.post.PostMessageFactory;
import nsfw.post.api.PostFetchException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostsCommand implements Command {

    private final PostMessageFactory postMessageFactory;

    @Override
    public SlashCommandBuilder toSlashCommandBuilder() {
        return SlashCommand.with("posts", "Search posts")
                .addOption(new SlashCommandOptionBuilder()
                        .setName("site")
                        .setDescription("Site to search posts for")
                        .setType(SlashCommandOptionType.STRING)
                        .setChoices(Arrays.stream(PostSite.values()).map(PostSite::toSlashCommandOptionChoice).toList())
                        .build())
                .addOption(new SlashCommandOptionBuilder()
                        .setName("tags")
                        .setType(SlashCommandOptionType.STRING)
                        .setDescription("Tags to search for")
                        .setAutocompletable(true)
                        .build());
    }

    @Override
    public void apply(SlashCommandCreateEvent event) throws CommandException, PostFetchException {
        String siteName = event.getSlashCommandInteraction().getOptionByName("site")
                .flatMap(SlashCommandInteractionOption::getStringValue)
                .orElseThrow(() -> new CommandException("Site is required"));

        String tags = event.getSlashCommandInteraction()
                .getOptionByName("tags")
                .flatMap(SlashCommandInteractionOption::getStringValue)
                .orElse("");

        PostSite postSite = PostSite.findByName(siteName);
        postSite.getPostApi().checkMaxTags(tags);

        postMessageFactory.createPost(event, tags, postSite);
    }

}
