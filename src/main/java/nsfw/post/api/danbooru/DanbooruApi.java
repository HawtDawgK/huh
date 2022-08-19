package nsfw.post.api.danbooru;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nsfw.enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.*;
import nsfw.post.api.generic.GenericPostApi;

import java.util.Optional;

@Slf4j
public class DanbooruApi extends GenericPostApi {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getBaseUrl() {
        return "https://danbooru.donmai.us/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.DANBOORU;
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
    public String getFetchByTagsAndPageUrl(String tags, int page) {
        return getBaseUrl() + "posts.xml?tags=" + PostApiUtil.encodeSpaces(tags) + "&page=" + page + "&limit=10";
    }

    @Override
    public int fetchCount(String tags) throws PostFetchException {
        String encodedTags = PostApiUtil.encodeSpaces(tags);
        String urlString = getBaseUrl() + "counts/posts.json?tags=" + encodedTags;

        JavaType javaType = TypeFactory.defaultInstance().constructType(DanbooruCountsResponse.class);

        try {
            DanbooruCountsResponse danbooruCountsResponse = PostApiUtil
                    .getResponseAsClass(javaType, objectMapper, urlString);
            return danbooruCountsResponse.getCounts().getPosts();
        } catch (PostApiException e) {
            throw new PostFetchException(e.getMessage(), e);
        }
    }

    @Override
    public JavaType getPostType() {
        return TypeFactory.defaultInstance().constructType(DanbooruPost.class);
    }

    @Override
    public JavaType getAutocompleteResultType() {
        return TypeFactory.defaultInstance().constructType(DanbooruAutocompleteResult.class);
    }

}
