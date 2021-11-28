package post;

import embed.PostNotFoundEmbed;
import enums.PostSite;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.interaction.SlashCommandInteraction;
import post.api.PostApi;

public class PostMessageFactory {

    private PostMessageFactory() { }

    public static void createPost(SlashCommandInteraction interaction, TextChannel textChannel, String tags, PostSite postSite) {
        PostApi postApi = postSite.getPostApi();

        int count = postSite.getPostApi().fetchCount(tags);

        if (count == 0) {
            textChannel.sendMessage(new PostNotFoundEmbed(tags));
            return;
        }

        Post post = postApi.fetchByTagsAndPage(tags, 0).orElse(null);

        if (post == null) {
            textChannel.sendMessage(new PostNotFoundEmbed(tags));
            return;
        }

        PostMessageable postMessageable = PostMessageable.fromPost(post);

        // Needed because the original response updater does not remove embeds
        Message message = interaction.createImmediateResponder()
                .setContent(postMessageable.getContent())
                .addEmbed(postMessageable.getEmbed())
                .addComponents(createActionRow())
                .respond().join()
                .update().join();

        PostMessage postMessage = new PostMessage(count, tags, message, postApi);
        postMessage.registerListener();
    }

    public static ActionRow createActionRow() {
        return ActionRow.of(Button.primary("next-page", "Next page"),
                        Button.primary("random-page", "Random page"),
                        Button.primary("previous-page", "Previous page"),
                        Button.success("add-favorite", "Add favorite"),
                        Button.danger("delete-message", "Delete message"));
    }

}
