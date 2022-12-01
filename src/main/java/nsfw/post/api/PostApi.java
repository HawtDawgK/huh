package nsfw.post.api;

import com.fasterxml.jackson.databind.JavaType;
import nsfw.commands.CommandException;
import nsfw.enums.PostSite;
import nsfw.post.Post;
import nsfw.post.autocomplete.AutocompleteException;
import nsfw.post.autocomplete.AutocompleteResult;

import java.util.List;
import java.util.Optional;

public interface PostApi {

    /**
     * @return the base url with trailing /
     */
    String getBaseUrl();

    PostSite getSite();

    default boolean hasAutocomplete() {
        return true;
    }

    JavaType getAutocompleteResultType();

    default String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "autocomplete.php?q=" + tags;
    }

    default Optional<Integer> getMaxCount() {
        return Optional.empty();
    }

    default Optional<Integer> getMaxTags() {
        return Optional.empty();
    }

    PostQueryResult<Post> getPosts(String url) throws PostFetchException;

    List<AutocompleteResult> autocomplete(String tags) throws AutocompleteException;

    JavaType getPostType();

    JavaType getPostQueryResultType();

    default int fetchCount(String tags) throws PostFetchException {
        return getPosts(getFetchByTagsAndPageUrl(tags, 0)).getCount();
    }

    default String getFetchByIdUrl(long id) {
        return getFetchByTagsAndPageUrl("id:" + id, 0);
    }

    default Post fetchById(long id) throws PostFetchException {
        PostQueryResult<Post> result = getPosts(getFetchByIdUrl(id));
        return result.getPosts().stream().findFirst()
                .orElseThrow(() -> new PostFetchException("No post found"));
    }

    default String getFetchByTagsAndPageUrl(String tags, int page) {
        return getBaseUrl() + "index.php?page=dapi&s=post&q=index&limit=1&tags="
            + PostApiUtil.encodeSpaces(tags) + "&pid=" + page;
    }

    default Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException {
        PostQueryResult<Post> result = getPosts(getFetchByTagsAndPageUrl(tags, page));
        return result.getPosts().stream().findFirst();
    }


    default void checkMaxTags(String tags) throws CommandException {
        Optional<Integer> optionalMaxTags = getMaxTags();
        if (optionalMaxTags.isPresent()) {
            int maxTags = optionalMaxTags.get();
            String[] tagParts = tags.split(" ");

            if (tagParts.length > maxTags) {
                throw new CommandException("Can search for max " + maxTags + ", you entered " + tagParts.length);
            }
        }
    }
}
