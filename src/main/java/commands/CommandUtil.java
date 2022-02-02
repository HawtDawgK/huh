package commands;

import api.ClientWrapper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.ApplicationInfo;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class CommandUtil {

    public static void createCommand(Command command) {
        ApplicationInfo applicationInfo = ClientWrapper.getClient().getApplicationInfo().block();

        if (applicationInfo == null) {
            log.error("Could not determine client application info");
            throw new RuntimeException("Could not determine client application info");
        }

        ClientWrapper.getClient().getGuilds()
                .subscribe(guild -> ClientWrapper.getClient().getRestClient().getApplicationService()
                    .createGuildApplicationCommand(applicationInfo.getId().asLong(), guild.getId().asLong(),
                            command.toApplicationCommandRequest()).block());
    }

    public static void checkNsfwChannel(ChatInputInteractionEvent event) throws CommandException {
        Interaction interaction = event.getInteraction();

        MessageChannel messageChannel = interaction.getChannel().block();
        if (messageChannel instanceof TextChannel) {
            TextChannel textChannel = (TextChannel) messageChannel;

            if (!textChannel.isNsfw()) {
                throw new CommandException("This command can only be used in NSFW channels.");
            }
        } else {
            log.error("Received ChatInputInteractionEvent for non-text channel {}", messageChannel);
            throw new CommandException("Could not find text channel");
        }
    }

}
