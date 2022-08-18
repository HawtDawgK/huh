package nsfw.post.api.e621;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nsfw.enums.PostSite;
import nsfw.post.api.generic.GenericPostApi;

public class E621Api extends GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://e621.net/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.E621;
    }

    @Override
    public String getFetchByTagsAndPageUrl(String tags, int page) {
        return getBaseUrl() + "posts.json?tags=" + tags + "&page=" + page + "&limit=1";
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        String urlWithoutMatch = getBaseUrl() + "tags/autocomplete.json";

        if (!tags.isEmpty()) {
            return urlWithoutMatch + "?search[name_matches]=" + tags;
        }

        return urlWithoutMatch;
    }

    @Override
    public JavaType getAutocompleteResultType() {
        return TypeFactory.defaultInstance().constructParametricType(E621AutocompleteResult.class, getPostType());
    }

    @Override
    public JavaType getPostType() {
        return TypeFactory.defaultInstance().constructType(E621Post.class);
    }
}
