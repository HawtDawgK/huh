package nsfw.post.api.danbooru;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.PostQueryResultImpl;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.PostQueryResult;
import nsfw.post.api.generic.GenericPostApi;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Optional;

@Slf4j
public class DanbooruApi extends GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://danbooru.donmai.us/";
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "tags.json?search[name_matches]=*" + tags + "*&limit=10&search[order]=count";
    }

    @Override
    public Optional<Integer> getMaxTags() {
        return Optional.of(2);
    }

    @Override
    public PostQueryResult<DanbooruPost> parsePostFetchResponse(PostFetchOptions options, String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (options.getId() != null) {
                DanbooruPost danbooruPost = objectMapper.readValue(responseBody, DanbooruPost.class);

                PostQueryResultImpl<DanbooruPost> postPostQueryResult = new PostQueryResultImpl<>();
                postPostQueryResult.setPosts(Collections.singletonList(danbooruPost));

                return postPostQueryResult;
            }

            return objectMapper.readValue(responseBody, DanbooruPostQueryResult.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int parseCount(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            DanbooruCountsResponse danbooruCountsResponse = objectMapper.readValue(responseBody, DanbooruCountsResponse.class);

            return danbooruCountsResponse.getCount();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUrl(PostFetchOptions options) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(getBaseUrl());

        if (options.isCounts()) {
           uriComponentsBuilder.path("counts/posts.json");
        } else if (options.getId() != null) {
            uriComponentsBuilder.path("posts/" + options.getId() + ".json");
        } else {
            uriComponentsBuilder.path("posts.json");
        }

        if (options.getTags() != null) {
            uriComponentsBuilder.queryParam("tags", options.getTags());
        }

        if (options.getPage() != null) {
            uriComponentsBuilder.queryParam("page", options.getPage());
        }

        return uriComponentsBuilder.toUriString();
    }

    @Override
    public JavaType getAutocompleteResultType() {
        return TypeFactory.defaultInstance().constructType(DanbooruAutocompleteResult.class);
    }

    @Override
    public JavaType getCountsResultType() {
        return TypeFactory.defaultInstance().constructType(DanbooruCountsResponse.class);
    }

    @Override
    public JavaType getPostQueryResultType() {
        return TypeFactory.defaultInstance().constructType(DanbooruPostQueryResult.class);
    }


}
