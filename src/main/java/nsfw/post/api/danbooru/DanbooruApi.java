package nsfw.post.api.danbooru;

import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.*;
import nsfw.post.api.generic.GenericPostApi;
import nsfw.post.autocomplete.AutocompleteResult;
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
            uriComponentsBuilder.path(options.getId() + ".json");
        } else {
            uriComponentsBuilder.path("posts.json");
        }

        if (options.getTags() != null) {
            uriComponentsBuilder.queryParam("tags", options.getTags());
        }

        return uriComponentsBuilder.toUriString();
    }

    @Override
    public AutocompleteResult getAutocompleteResultType() {
        return new DanbooruAutocompleteResult();
    }

}
