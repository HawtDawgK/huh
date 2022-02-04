package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import post.PostMessageFactory;
import reactor.core.publisher.Mono;

public class FavoritesCommand implements Command {

    @Override
    public ApplicationCommandRequest toApplicationCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name("favorites")
                .description("Show a user's favorites")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("user")
                        .description("User to search favorites you, will be you if unfilled")
                        .type(ApplicationCommandOption.Type.USER.getValue())
                        .build())
                .build();
    }

    @Override
    public Mono<Void> apply(ChatInputInteractionEvent event) throws CommandException {
        CommandUtil.checkNsfwChannel(event);

        User user = event.getOption("user")
                .flatMap(o -> o.getValue()
                        .map(ApplicationCommandInteractionOptionValue::asUser)
                        .map(Mono::block))
                .orElse(event.getInteraction().getUser());

        return PostMessageFactory.createListPostFromFavorites(event, user);
    }
}
