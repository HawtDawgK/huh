package nsfw.commands;

import nsfw.api.ClientWrapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.ApplicationInfo;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import nsfw.post.api.PostApi;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@NoArgsConstructor
public class CommandUtil {

    @Autowired
    private ClientWrapper clientWrapper;

    public void createCommand(Command command) {
        ApplicationInfo applicationInfo = clientWrapper.getApi().getApplicationInfo().join();

        if (applicationInfo == null) {
            log.error("Could not determine client application info");
            throw new RuntimeException("Could not determine client application info");
        }

        clientWrapper.getApi().getServers()
                .forEach(guild -> command.toSlashCommandBuilder().createForServer(guild).join());
    }

    public static void checkNsfwChannel(SlashCommandInteraction interaction) throws CommandException {
        Optional<TextChannel> channel = interaction.getChannel();

        if (channel.isPresent() && channel.get() instanceof ServerTextChannel) {
            ServerTextChannel textChannel = (ServerTextChannel) channel.get();

            if (!textChannel.isNsfw()) {
                throw new CommandException("This command can only be used in NSFW channels.");
            }
        } else {
            log.error("Received ChatInputInteractionEvent for non-text channel {}", channel);
            throw new CommandException("Could not find text channel.");
        }
    }

    public static void checkMaxTags(PostApi postApi, String tags) throws CommandException {
        Optional<Integer> optionalMaxTags = postApi.getMaxTags();
        if (optionalMaxTags.isPresent()) {
            int maxTags = optionalMaxTags.get();
            String[] tagParts = tags.split(" ");

            if (tagParts.length > maxTags) {
                throw new CommandException("Can search for max " + maxTags + ", you entered " + tagParts.length);
            }
        }
    }

}