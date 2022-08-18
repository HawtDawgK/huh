package nsfw.post.api.generic;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.Post;
import nsfw.post.api.*;
import nsfw.post.autocomplete.AutocompleteException;
import nsfw.post.autocomplete.AutocompleteResult;
import nsfw.post.autocomplete.AutocompleteResultImpl;

import java.util.List;

@Slf4j
public abstract class GenericPostApi implements PostApi {

    private static final ObjectMapper objectMapper = new XmlMapper();

    @Override
    public JavaType getPostType() {
        return TypeFactory.defaultInstance().constructType(GenericPost.class);
    }

    @Override
    public JavaType getAutocompleteResultType() {
        return TypeFactory.defaultInstance().constructType(AutocompleteResultImpl.class);
    }

    @Override
    public JavaType getPostQueryResultType() {
        return TypeFactory.defaultInstance().constructParametricType(GenericPostQueryResult.class, getPostType());
    }

    public PostQueryResult<Post> getPosts(String urlString) throws PostFetchException {
        try {
            PostQueryResult<Post> result = PostApiUtil
                    .getResponseAsClass(getPostQueryResultType(), objectMapper, urlString);

            result.getPosts().forEach(p -> ((GenericPost) p).setSite(getSite()));

            return result;
        } catch (PostApiException e) {
            throw new PostFetchException("Error fetching post", e);
        }
    }

    @Override
    public List<AutocompleteResult> autocomplete(String tags) throws AutocompleteException {
        try {
            String autocompleteUrl = getAutocompleteUrl(tags);
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, getAutocompleteResultType());
            return PostApiUtil.getResponseAsClass(javaType, new ObjectMapper(), autocompleteUrl);
        } catch (PostApiException e) {
            throw new AutocompleteException("Error fetching autocomplete", e);
        }
    }
}
