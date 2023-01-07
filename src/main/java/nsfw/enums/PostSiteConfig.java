package nsfw.enums;

import com.fasterxml.jackson.databind.JavaType;
import nsfw.post.api.PostFetchOptions;

import java.util.Optional;

public interface PostSiteConfig {

    /**
     * @return the base url with trailing /
     */
    String getBaseUrl();

    String getUrl(PostFetchOptions postFetchOptions);

    Optional<Integer> getMaxCount();

    Optional<Integer> getMaxTags();

    JavaType getPostType();

    JavaType getPostQueryResultType();

    JavaType getAutocompleteResultType();

    String getAutocompleteUrl(String tags);

}
