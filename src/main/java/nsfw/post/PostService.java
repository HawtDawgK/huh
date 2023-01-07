package nsfw.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import nsfw.post.api.*;
import nsfw.post.cache.PostCache;
import nsfw.util.TagUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCache postCache;

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    private final XmlMapper xmlMapper;

    private final ApplicationEventPublisher eventPublisher;

    public Post resolve(PostResolvable postResolvable) throws PostFetchException {
        Post cachedPost = postCache.get(postResolvable);

        if (cachedPost != null) {
            return cachedPost;
        }

        PostFetchOptions postFetchOptions = PostFetchOptions.builder()
                .id(postResolvable.getPostId())
                .postSite(postResolvable.getPostSite())
                .build();

        return fetchPost(postFetchOptions);
    }

    public Post fetchPost(PostFetchOptions options) throws PostFetchException {
        try {
            PostApi postApi = options.getPostSite().getPostApi();
            String url = postApi.getUrl(options);

            String responseBody = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatus::isError, response -> Mono.just(new PostFetchException("Error fetching post")))
                    .bodyToMono(String.class)
                    .block();

            PostQueryResult postQueryResult;

            if (postApi.isJson()) {
                postQueryResult = objectMapper.readValue(responseBody, postApi.getPostQueryResultType());
            } else {
                postQueryResult = xmlMapper.readValue(responseBody, postApi.getPostQueryResultType());
            }

            if (postQueryResult.getPosts().isEmpty()) {
                throw new PostFetchException("Could not find posts");
            }

            Post post = postQueryResult.getPosts().get(0);

            if (TagUtil.hasDisallowedTags(post.getTags())) {
                throw new PostFetchException("Post contains disallowed tags");
            }

            return post;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public int fetchCount(PostFetchOptions options) throws PostFetchException {
        try {
            PostApi postApi = options.getPostSite().getPostApi();
            String url = postApi.getUrl(options);

            String responseBody = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatus::isError, response -> Mono.just(new PostFetchException("Error fetching posts")))
                    .bodyToMono(String.class)
                    .block();

            CountResult countResult;
            if (postApi.isJson()) {
                countResult = objectMapper.readValue(responseBody, postApi.getCountsResultType());
            } else {
                countResult = xmlMapper.readValue(responseBody, postApi.getCountsResultType());
            }
            return countResult.getCount();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new PostFetchException("Error fetching count", e);
        }
    }
}
