package post.api.hypnohub;

import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import enums.PostSite;
import java.util.Collections;
import post.Post;
import post.api.PostApiUtil;
import post.api.PostFetchException;
import post.api.generic.GenericApi;
import post.autocomplete.AutocompleteException;

import java.util.List;
import java.util.Optional;

public class HypnohubApi extends GenericApi {

    @Override
    public String getBaseUrl() {
        return "https://hypnohub.net/";
    }

    @Override
    public boolean hasAutocomplete() {
        return true;
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "tag/index.xml?limit=10&order=count&name=" + tags;
    }

    @Override
    public PostSite getSite() {
        return PostSite.HYPNOHUB;
    }

    @Override
    public Optional<Integer> getMaxCount() {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getMaxTags() {
        return Optional.empty();
    }

    @Override
    public int fetchCount(String tags) throws PostFetchException {
//        String encodedTags = PostApiUtil.encodeSpaces(tags);
//        String url = getBaseUrl() + "post/index.xml?limit=0" + tags;

        return 0;
    }

    @Override
    public Optional<Post> fetchById(long id) throws PostFetchException {
        return Optional.empty();
    }

    @Override
    public Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException {
        return Optional.empty();
    }

    @Override
    public List<ApplicationCommandOptionChoiceData> autocomplete(String input) throws AutocompleteException {
        return Collections.emptyList();
    }
}
