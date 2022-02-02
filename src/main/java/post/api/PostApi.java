package post.api;

import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import post.Post;
import post.autocomplete.AutocompleteException;

import java.util.List;
import java.util.Optional;

public interface PostApi {

    Optional<Post> fetchById(long id) throws PostFetchException;

    Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException;

    int fetchCount(String tags) throws PostFetchException;

    boolean hasAutocomplete();

    List<ApplicationCommandOptionChoiceData> autocomplete(String input) throws AutocompleteException;

    int getMaxCount();
}
