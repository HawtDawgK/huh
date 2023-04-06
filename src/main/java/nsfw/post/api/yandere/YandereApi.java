package nsfw.post.api.yandere;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.generic.GenericPostApi;
import org.springframework.web.util.UriComponentsBuilder;

public class YandereApi extends GenericPostApi  {

    @Override
    public String getBaseUrl() {
        return "https://yande.re";
    }

    @Override
    public JavaType getAutocompleteResultType() {
        return TypeFactory.defaultInstance().constructType(YandereAutocompleteResult.class);
    }

    @Override
    public JavaType getPostQueryResultType() {
        return TypeFactory.defaultInstance().constructType(YanderePostQueryResult.class);
    }

    @Override
    public JavaType getCountsResultType() {
        return getPostQueryResultType();
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return UriComponentsBuilder.fromUriString(getBaseUrl())
                .path("tag.xml")
                .queryParam("limit", "25")
                .queryParam("name", tags)
                .toUriString();
    }

    @Override
    public String getUrl(PostFetchOptions options) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getBaseUrl());
        builder.path("post.xml");

        builder.queryParam("limit", "1");

        Long id = options.getId();

        if (id != null) {
            builder.queryParam("tags", "id:" + id);
        } else {
            if (options.getPage() != null) {
                builder.queryParam("page", options.getPage());
            }
            if (options.getTags() != null) {
                builder.queryParam("tags", options.getTags());
            }
        }

        return builder.toUriString();
    }
}
