package commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import post.PostMessageFactory;
import post.api.PostFetchException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class PostsCommand implements Command {

    @Override
    public ApplicationCommandRequest toApplicationCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name("posts")
                .description("Search posts")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("site")
                        .description("Site to search posts for")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .choices(Arrays.stream(PostSite.values()).map(PostSite::toApplicationCommand).collect(Collectors.toList()))
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("tags")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("Tags to search for")
                        .autocomplete(true)
                        .build())
                .build();
    }

    @Override
    public Mono<Void> apply(ChatInputInteractionEvent event) throws CommandException, PostFetchException {
        CommandUtil.checkNsfwChannel(event);

        String siteName = event.getOption("site")
                .flatMap(o -> o.getValue().map(ApplicationCommandInteractionOptionValue::asString))
                .orElseThrow(() -> new CommandException("Site is required"));

        String tags = event.getOption("tags")
                .flatMap(o -> o.getValue().map(ApplicationCommandInteractionOptionValue::asString))
                .orElse("");

        PostSite postSite = PostSite.findByName(siteName);
        CommandUtil.checkMaxTags(postSite.getPostApi(), tags);

        return PostMessageFactory.createPost(event, tags, postSite);
    }

}
