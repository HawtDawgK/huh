package commands;

import enums.PostSite;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import post.PostMessageFactory;

import java.util.Arrays;
import java.util.Optional;

public class NsfwCommand implements ServerCommand {

    public SlashCommandBuilder toSlashCommandBuilder() {
        SlashCommandOptionBuilder siteOptionBuilder = new SlashCommandOptionBuilder()
                .setName("site")
                .setDescription("The site to search images for")
                .setType(SlashCommandOptionType.STRING);

        Arrays.stream(PostSite.values()).forEach(ps -> siteOptionBuilder.addChoice(ps.toSlashCommandOptionChoice()));

        return SlashCommand.with("posts", "Searches for NSFW images")
                .addOption(siteOptionBuilder.build())
                .addOption(new SlashCommandOptionBuilder()
                        .setName("tags")
                        .setDescription("search tags")
                        .setType(SlashCommandOptionType.STRING)
                        .build());
    }

    @Override
    public void apply(SlashCommandCreateEvent event) throws CommandException {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        TextChannel textChannel = interaction.getChannel().orElseThrow(() -> new CommandException("No channel"));

        Optional<ServerTextChannel> optSrvTxtChannel = textChannel.asServerTextChannel();
        if (optSrvTxtChannel.isPresent()) {
            ServerTextChannel serverTextChannel = optSrvTxtChannel.get();

            if (!serverTextChannel.isNsfw()) {
                throw new CommandException("Channel is not NSFW!");
            }
        }

        String siteName = interaction.getFirstOptionStringValue().orElse(PostSite.RULE34.getName());
        String tags = interaction.getSecondOptionStringValue().orElse("");

        PostSite postSite = PostSite.findByName(siteName);

        PostMessageFactory.createPost(interaction, textChannel, tags, postSite);
    }
}
