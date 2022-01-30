package post.api;

import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import post.Post;

import java.util.List;
import java.util.Optional;

public interface PostApi {

    Optional<Post> fetchById(long id);

    Optional<Post> fetchByTagsAndPage(String tags, int page);

    int fetchCount(String tags);

    boolean hasAutocomplete();

    List<ApplicationCommandOptionChoiceData> autocomplete(String input);

    int getMaxCount();
}
