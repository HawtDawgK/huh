package nsfw.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsfw.embed.EmbedService;
import nsfw.post.api.PostFetchException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {

    private static final Map<String, Command> commandMap = new HashMap<>();

    private final DiscordApi discordApi;

    private final EmbedService embedService;

    private final PostsCommand postsCommand;

    private final HistoryCommand historyCommand;

    private final FavoritesCommand favoritesCommand;

    @PostConstruct
    public void init() {
        commandMap.put("posts", postsCommand);
        commandMap.put("history", historyCommand);
        commandMap.put("favorites", favoritesCommand);

        commandMap.values().forEach(command -> discordApi.getServers()
                .forEach(guild -> command.toSlashCommandBuilder().createForServer(guild).join()));

        discordApi.addSlashCommandCreateListener(this::handleSlashCommand);
    }

    private void handleSlashCommand(SlashCommandCreateEvent event) {
        try {
            checkNsfwChannel(event.getSlashCommandInteraction());
            commandMap.get(event.getSlashCommandInteraction().getCommandName()).apply(event);
        } catch (CommandException | PostFetchException e) {
            log.error(e.getMessage(), e);
            event.getInteraction().createImmediateResponder()
                    .addEmbed(embedService.createErrorEmbed(e.getMessage()))
                    .respond().join();
        }
    }

    public void checkNsfwChannel(SlashCommandInteraction interaction) throws CommandException {
        Optional<TextChannel> channel = interaction.getChannel();

        if (channel.isPresent() && channel.get() instanceof ServerTextChannel serverTextChannel) {
            if (!serverTextChannel.isNsfw()) {
                throw new CommandException("This command can only be used in NSFW channels.");
            }
        } else {
            log.error("Received ChatInputInteractionEvent for non-text channel {}", channel);
            throw new CommandException("Could not find text channel.");
        }
    }
}
