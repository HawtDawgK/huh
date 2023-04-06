package nsfw.post.api.generic;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.PostApi;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.autocomplete.AutocompleteResultImpl;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public abstract class GenericPostApi implements PostApi {

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "/autocomplete.php?q=" + tags;
    }

    @Override
    public JavaType getCountsResultType() {
        return getPostQueryResultType();
    }

    @Override
    public JavaType getPostQueryResultType() {
        return TypeFactory.defaultInstance()
                .constructParametricType(GenericPostQueryResult.class, GenericPost.class);
    }

    @Override
    public JavaType getAutocompleteResultType() {
        return TypeFactory.defaultInstance().constructType(AutocompleteResultImpl.class);
    }

    @Override
    public boolean isJson() {
        return false;
    }

    @Override
    public String getUrl(PostFetchOptions options) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getBaseUrl());

        builder.queryParam("page", "dapi");
        builder.queryParam("s", "post");
        builder.queryParam("q", "index");
        builder.queryParam("limit", "1");

        Long id = options.getId();

        if (id != null) {
            builder.queryParam("id", id.toString());
        } else {
            if (options.getPage() != null) {
                builder.queryParam("pid", options.getPage());
            }
            if (options.getTags() != null) {
                builder.queryParam("tags", options.getTags());
            }
        }

        return builder.toUriString();
    }
}
