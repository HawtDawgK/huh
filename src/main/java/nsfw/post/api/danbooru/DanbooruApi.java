package nsfw.post.api.danbooru;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import nsfw.enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.Post;
import nsfw.post.api.*;
import nsfw.post.autocomplete.AutocompleteResult;

import java.util.Optional;

@Slf4j
public class DanbooruApi extends GenericPostApi {

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

        JavaType javaType = new ObjectMapper().getTypeFactory().constructType(DanbooruCountsResponse.class);

        try {
            DanbooruCountsResponse danbooruCountsResponse = PostApiUtil
                    .getResponseAsClass(javaType, new ObjectMapper(), urlString);
            return danbooruCountsResponse.getCounts().getPosts();
        } catch (PostApiException e) {
            throw new PostFetchException(e.getMessage(), e);
        }
    }

    @Override
    public Class<? extends Post> getPostClass() {
        return DanbooruPost.class;
    }

    @Override
    public Class<? extends AutocompleteResult> getAutocompleteResultClass() {
        return DanbooruAutocompleteResult.class;
    }

}
