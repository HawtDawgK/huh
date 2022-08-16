package nsfw.post.api;

import nsfw.enums.PostSite;
import nsfw.post.Post;
import nsfw.post.autocomplete.AutocompleteException;
import nsfw.post.autocomplete.AutocompleteResult;
import org.javacord.api.interaction.SlashCommandOptionChoice;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface PostApi {

    /**
     * @return the base url with trailing /
     */
    String getBaseUrl();

    PostSite getSite();

    default boolean hasAutocomplete() {
        return true;
    }

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

    AutocompleteResult[] getAutocompleteResult(String url) throws AutocompleteException;

    Class<? extends AutocompleteResult> getAutocompleteResultClass();

    Class<? extends Post> getPostClass();

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

    default List<SlashCommandOptionChoice> autocomplete(String input) throws AutocompleteException {
        return Arrays.stream(getAutocompleteResult(input))
                .map(x -> SlashCommandOptionChoice.create(x.getLabel(), x.getValue()))
                .collect(Collectors.toList());
    }

}
