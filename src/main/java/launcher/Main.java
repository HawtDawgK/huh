package launcher;

import api.Api;
import commands.CommandException;
import commands.CommandUtil;
import commands.NsfwCommand;
import embed.ErrorEmbed;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    public static void main(String[] args) {
        NsfwCommand nsfwCommand = new NsfwCommand();
        CommandUtil.createForServers(nsfwCommand);

        Api.getAPI().addSlashCommandCreateListener(event -> {
            try {
                nsfwCommand.apply(event);
            } catch (CommandException e) {
                event.getSlashCommandInteraction().createImmediateResponder()
                        .addEmbed(new ErrorEmbed().setDescription(e.getMessage()))
                        .respond().join();
            }
        });
    }
}
