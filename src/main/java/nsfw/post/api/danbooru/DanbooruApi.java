package nsfw.post.api.danbooru;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.generic.GenericPostApi;
import org.springframework.web.util.UriComponentsBuilder;

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
    public boolean isJson() {
        return true;
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
