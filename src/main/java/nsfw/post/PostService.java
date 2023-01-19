package nsfw.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import nsfw.post.api.*;
import nsfw.post.cache.PostCache;
import nsfw.post.history.HistoryEvent;
import nsfw.util.TagUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCache postCache;

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    private final XmlMapper xmlMapper;

    private final ApplicationEventPublisher eventPublisher;

    public PostFetchResult fetchPost(@Nullable TextChannel textChannel, PostFetchOptions options) {
        try {
            PostApi postApi = options.getPostSite().getPostApi();

            ResponseEntity<String> responseEntity = webClient.get().uri(postApi.getUrl(options))
                    .retrieve().toEntity(String.class).block();

            if (responseEntity == null || responseEntity.getStatusCode().isError()) {
                return new PostFetchResult(null, true, "Error fetching post");
            }

            PostQueryResult postQueryResult;

            if (postApi.isJson()) {
                postQueryResult = objectMapper.readValue(responseEntity.getBody(), postApi.getPostQueryResultType());
            } else {
                postQueryResult = xmlMapper.readValue(responseEntity.getBody(), postApi.getPostQueryResultType());
            }

            if (postQueryResult.getPosts().isEmpty()) {
                return new PostFetchResult(null, true, "Could not find any posts.");
            }

            Post post = postQueryResult.getPosts().get(0);

            List<String> disallowedTags = TagUtil.getDisallowedTags(post.getTags());
            if (!disallowedTags.isEmpty()) {
                String errorMessage = "Post contains disallowed tags: " + String.join(",", disallowedTags);
                return new PostFetchResult(null, true, errorMessage);
            }

            PostResolvableEntry postResolvableEntry = new PostResolvableEntry(post.getId(), options.getPostSite(),
                    Instant.now());

            if (textChannel != null) {
                eventPublisher.publishEvent(new HistoryEvent(postResolvableEntry, textChannel));
            }

            postCache.put(post, postApi.getSite());
            return new PostFetchResult(post, false, null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public int fetchCount(PostFetchOptions options) {
        try {
            PostApi postApi = options.getPostSite().getPostApi();
            String url = postApi.getUrl(options);

            ResponseEntity<String> responseEntity = webClient.get().uri(url)
                    .retrieve().toEntity(String.class).block();

            if (responseEntity == null || responseEntity.getStatusCode().isError()) {
                return 0;
            }

            CountResult countResult;
            if (postApi.isJson()) {
                countResult = objectMapper.readValue(responseEntity.getBody(), postApi.getCountsResultType());
            } else {
                countResult = xmlMapper.readValue(responseEntity.getBody(), postApi.getCountsResultType());
            }
            return countResult.getCount();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
