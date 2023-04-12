package nsfw.post;

import lombok.RequiredArgsConstructor;
import nsfw.db.PostEntity;
import nsfw.post.api.PostApi;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.PostQueryResult;
import nsfw.post.cache.PostCache;
import nsfw.post.history.HistoryEvent;
import nsfw.util.TagUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCache postCache;

    private final WebClient webClient;

    private final ApplicationEventPublisher eventPublisher;

    public PostFetchResult fetchPost(@Nullable TextChannel textChannel, PostFetchOptions options) {
        if (isInCache(options)) {
            return fetchFromCache(options);
        }

        PostApi postApi = options.getPostSite().getPostApi();

        ResponseEntity<String> responseEntity = webClient.get()
                .uri(postApi.getUrl(options))
                .retrieve().toEntity(String.class).block();

        if (responseEntity == null || responseEntity.getStatusCode().isError()) {
            return new PostFetchResult(null, true, "Error fetching post");
        }

        PostQueryResult<? extends Post> postQueryResult = postApi.parsePostFetchResponse(options, responseEntity.getBody());

        if (postQueryResult.getPosts().isEmpty()) {
            return new PostFetchResult(null, true, "Could not find any posts.");
        }

        Post post = postQueryResult.getPosts().get(0);
        post.setPostSite(options.getPostSite());

        List<String> disallowedTags = TagUtil.getDisallowedTags(post.getTags());
        if (!disallowedTags.isEmpty()) {
            String errorMessage = "Post contains disallowed tags: " + String.join(",", disallowedTags);
            return new PostFetchResult(null, true, errorMessage);
        }

        if (textChannel != null) {
            createHistoryEvent(post, textChannel);
        }

        postCache.put(post);
        return new PostFetchResult(post, false, null);
    }

    private void createHistoryEvent(Post post, TextChannel textChannel) {
        PostEntity postEntityKey = new PostEntity();
        postEntityKey.setSite(post.getPostSite());
        postEntityKey.setPostId(post.getId());

        eventPublisher.publishEvent(new HistoryEvent(postEntityKey, textChannel));
    }

    public int fetchCount(PostFetchOptions options) {
        PostApi postApi = options.getPostSite().getPostApi();
        String url = postApi.getUrl(options);

        ResponseEntity<String> responseEntity = webClient.get().uri(url)
                .retrieve().toEntity(String.class).block();

        if (responseEntity == null || responseEntity.getStatusCode().isError()) {
            return 0;
        }

        return postApi.parseCount(responseEntity.getBody());
    }

    private PostFetchResult fetchFromCache(PostFetchOptions options) {
        if (options.getId() == null) {
            return new PostFetchResult(null, false, "");
        }

        PostEntity postEntity = new PostEntity();
        postEntity.setPostId(options.getId());
        postEntity.setSite(options.getPostSite());
        Post post = postCache.get(postEntity);

        return new PostFetchResult(post, false, "");
    }

    public boolean isInCache(PostFetchOptions options) {
        if (options.getId() == null) {
            return false;
        }

        PostEntity postEntity = new PostEntity();
        postEntity.setPostId(options.getId());
        postEntity.setSite(options.getPostSite());
        return postCache.hasPost(postEntity);
    }
}
