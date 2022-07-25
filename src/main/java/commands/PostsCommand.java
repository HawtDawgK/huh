package commands;

import enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import post.PostMessageFactory;
import post.api.PostFetchException;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class PostsCommand implements Command {

    @Override
    public SlashCommandBuilder toSlashCommandBuilder() {
        return new SlashCommandBuilder()
                .setName("posts")
                .setDescription("Search posts")
                .addOption(new SlashCommandOptionBuilder()
                        .setName("site")
                        .setDescription("Site to search posts for")
                        .setType(SlashCommandOptionType.STRING)
                        .setChoices(Arrays.stream(PostSite.values()).map(PostSite::toApplicationCommand).collect(Collectors.toList()))
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
        CommandUtil.checkNsfwChannel(event.getInteraction());

        String siteName = event.getSlashCommandInteraction().getOptionStringValueByName("site")
                .orElseThrow(() -> new CommandException("Site is required"));

        String tags = event.getSlashCommandInteraction()
                .getOptionStringValueByName("tags")
                .orElse("");

        PostSite postSite = PostSite.findByName(siteName);
        CommandUtil.checkMaxTags(postSite.getPostApi(), tags);

        PostMessageFactory.createPost(event, tags, postSite);
    }

}
