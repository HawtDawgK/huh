package post.api;

import enums.PostSite;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import post.Post;
import post.autocomplete.AutocompleteException;

import java.util.List;
import java.util.Optional;

public interface PostApi {

    /**
     * @return the base url with trailing /
     */
    String getBaseUrl();

    boolean hasAutocomplete();

    String getAutocompleteUrl(String tags);

    PostSite getSite();

    Optional<Integer> getMaxCount();

    Optional<Integer> getMaxTags();

    int fetchCount(String tags) throws PostFetchException;

    Optional<Post> fetchById(long id) throws PostFetchException;

    Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException;

    List<SlashCommandOptionChoice> autocomplete(String input) throws AutocompleteException;

}
