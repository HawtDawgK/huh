package post;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import embed.ErrorEmbed;
import embed.PostNotFoundEmbed;
import enums.PostSite;

import post.api.PostApi;
import post.api.rule34.Rule34Api;
import post.history.PostHistory;
import reactor.core.publisher.Mono;

import java.util.List;

public class PostMessageFactory {

    private PostMessageFactory() { }

    public static Mono<Void> createPost(ChatInputInteractionEvent event, String tags, PostSite postSite) {
        PostApi postApi = postSite.getPostApi();
        int count = postApi.fetchCount(tags);

        if (count == 0) {
            return event.reply().withEmbeds(PostNotFoundEmbed.create(tags));
        }

        PostMessage postMessage = new PostMessage(count, tags, event, postApi);
        postMessage.initReply();

        PostMessages.addPost(postMessage);

        return Mono.empty();
    }

    public static Mono<Void> createListPost(ChatInputInteractionEvent event) {
        MessageChannel messageChannel = event.getInteraction().getChannel().block();

        List<PostResolvable> postHistoryFromChannel = PostHistory.getHistory(messageChannel);

        if (postHistoryFromChannel.isEmpty()) {
            return event.reply().withEmbeds(ErrorEmbed.create("No posts in history"));
        }

        PostMessage postMessage = new PostListMessage(postHistoryFromChannel, null, event, new Rule34Api());
        postMessage.initReply();

        PostMessages.addPost(postMessage);

        return Mono.empty();
    }

}
