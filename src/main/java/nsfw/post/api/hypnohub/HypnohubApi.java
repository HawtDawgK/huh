package nsfw.post.api.hypnohub;

import nsfw.enums.PostSite;
import nsfw.post.api.PostApi;
import nsfw.post.autocomplete.AutocompleteResult;

public class HypnohubApi implements PostApi<nsfw.post.api.generic.PostQueryResult<HypnohubPost>, HypnohubPost, AutocompleteResult> {

    @Override
    public PostSite getSite() {
        return PostSite.HYPNOHUB;
    }

    @Override
    public String getBaseUrl() {
        return "https://hypnohub.net/";
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "public/autocomplete.php?q=" + tags;
    }

    @Override
    public String getFetchByIdUrl(long id) {
        return getBaseUrl() + "index.php?page=dapi&s=post&q=index&id=" + id;
    }

    @Override
    public String getFetchByTagsAndPageUrl(String tags, int page) {
        return getBaseUrl() + "index.php?page=dapi&s=post&q=index&tags=" + tags + "&pid=" + page;
    }
}
