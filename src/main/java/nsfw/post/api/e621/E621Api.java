package nsfw.post.api.e621;

import nsfw.enums.PostSite;
import nsfw.post.api.GenericPostApi;
import nsfw.post.autocomplete.AutocompleteResult;

public class E621Api extends GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://e621.net/";
    }

    @Override
    public String getFetchByTagsAndPageUrl(String tags, int page) {
        return "posts.json?tags=" + tags + "&page=" + page + "&limit=1";
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "tags/autocomplete.json?search[name_matches]=" + tags;
    }

    @Override
    public Class<? extends AutocompleteResult> getAutocompleteResultClass() {
        return E621AutocompleteResult.class;
    }

    @Override
    public PostSite getSite() {
        return PostSite.E621;
    }
}
