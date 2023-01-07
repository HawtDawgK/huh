package nsfw.post.api.e621;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nsfw.enums.PostSite;
import nsfw.post.api.PostApi;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.PostQueryResult;
import nsfw.post.autocomplete.AutocompleteResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

public class E621Api implements PostApi {

    @Override
    public String getBaseUrl() {
        return "https://e621.net/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.E621;
    }

    @Override
    public JavaType getPostQueryResultType() {
        return TypeFactory.defaultInstance().constructType(E621PostQueryResult.class);
    }

    @Override
    public JavaType getCountsResultType() {
        return TypeFactory.defaultInstance().constructType(PostQueryResult.class);
    }

    @Override
    public String getUrl(PostFetchOptions postFetchOptions) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(getBaseUrl());
        uriComponentsBuilder.path("posts.json");

        uriComponentsBuilder.queryParamIfPresent("tags", Optional.ofNullable(postFetchOptions.getTags()));

        String tags = "";
        String url = "";

        if (postFetchOptions.getId() != null) {
            tags += "";
        }
        if (postFetchOptions.getId() != null) {
            url += "tags=id:" + postFetchOptions.getId();
        }
        if (postFetchOptions.getPage() != null) {
            url += "&pid=";
        }

        return  url + tags + "&page=" + postFetchOptions.getPage() + "&limit=1";
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        String urlWithoutMatch = getBaseUrl() + "tags/autocomplete.json";

        if (!tags.isEmpty()) {
            return urlWithoutMatch + "?search[name_matches]=" + tags;
        }

        return urlWithoutMatch;
    }

    @Override
    public boolean isJson() {
        return false;
    }

    @Override
    public Class<? extends AutocompleteResult> getAutocompleteResultType() {
        return E621AutocompleteResult.class;
    }

}
