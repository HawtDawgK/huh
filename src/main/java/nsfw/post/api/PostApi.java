package nsfw.post.api;

import com.fasterxml.jackson.databind.JavaType;
import nsfw.commands.CommandException;
import nsfw.post.autocomplete.AutocompleteResult;

import java.util.Optional;

public interface PostApi {

    /**
     * @return the base url with trailing /
     */
    String getBaseUrl();

    AutocompleteResult getAutocompleteResultType();

    default String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "autocomplete.php?q=" + tags;
    }

    default Optional<Integer> getMaxCount() {
        return Optional.empty();
    }

    default Optional<Integer> getMaxTags() {
        return Optional.empty();
    }

    boolean isJson();

    JavaType getPostQueryResultType();

    JavaType getCountsResultType();

    String getUrl(PostFetchOptions options);

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
