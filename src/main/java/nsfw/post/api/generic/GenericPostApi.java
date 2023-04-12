package nsfw.post.api.generic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.PostApi;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.PostQueryResult;
import nsfw.post.autocomplete.AutocompleteResultImpl;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public abstract class GenericPostApi implements PostApi {

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "/autocomplete.php?q=" + tags;
    }

    @Override
    public JavaType getAutocompleteResultType() {
        return TypeFactory.defaultInstance().constructType(AutocompleteResultImpl.class);
    }

    @Override
    public JavaType getCountsResultType() {
        return getPostQueryResultType();
    }

    @Override
    public JavaType getPostQueryResultType() {
        return TypeFactory.defaultInstance().constructType(GenericPostQueryResult.class);
    }

    @Override
    public PostQueryResult<? extends GenericPost> parsePostFetchResponse(PostFetchOptions options, String responseBody) {
        try {
            return new XmlMapper().readValue(responseBody, getPostQueryResultType());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int parseCount(String responseBody) {
        try {
            XmlMapper xmlMapper = new XmlMapper();

            PostQueryResult<GenericPost> countResult = xmlMapper.readValue(responseBody, getCountsResultType());
            return countResult.getCount();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
