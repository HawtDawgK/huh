package nsfw.post.api;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.Post;
import nsfw.post.autocomplete.AutocompleteException;
import nsfw.post.autocomplete.AutocompleteResult;
import nsfw.post.autocomplete.AutocompleteResultImpl;

@Slf4j
public abstract class GenericPostApi implements PostApi {

    private static final ObjectMapper objectMapper = new XmlMapper();

    @Override
    public Class<? extends Post> getPostClass() {
        return Post.class;
    }

    @Override
    public Class<? extends AutocompleteResult> getAutocompleteResultClass() {
        return AutocompleteResultImpl.class;
    }

    public PostQueryResult<Post> getPosts(String urlString) throws PostFetchException {
        try {
            JavaType javaType = objectMapper.getTypeFactory()
                    .constructParametricType(PostQueryResult.class, getPostClass());

            PostQueryResult<Post> result = PostApiUtil.getResponseAsClass(javaType, objectMapper, urlString);
            result.getPosts().forEach(p -> p.setSite(getSite()));

            return result;
        } catch (PostApiException e) {
            throw new PostFetchException("Error fetching post", e);
        }
    }

    public AutocompleteResult[] getAutocompleteResult(String tags) throws AutocompleteException {
        try {
            String autocompleteUrl = getAutocompleteUrl(tags);
            JavaType javaType = objectMapper.getTypeFactory().constructArrayType(getAutocompleteResultClass());
            return PostApiUtil.getResponseAsClass(javaType, new ObjectMapper(), autocompleteUrl);
        } catch (PostApiException e) {
            throw new AutocompleteException("Error fetching autocomplete", e);
        }
    }
}
