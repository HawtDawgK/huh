package nsfw.commands;

import lombok.RequiredArgsConstructor;
import nsfw.enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import nsfw.post.message.PostMessageFactory;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.springframework.stereotype.Component;

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
                        .setRequired(true)
                        .setType(SlashCommandOptionType.STRING)
                        .setChoices(PostSite.slashCommandOptionChoices())
                        .build())
                .addOption(new SlashCommandOptionBuilder()
                        .setName("tags")
                        .setType(SlashCommandOptionType.STRING)
                        .setDescription("Tags to search for")
                        .setAutocompletable(true)
                        .build());
    }

    @Override
    public void apply(SlashCommandCreateEvent event) throws CommandException {
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
