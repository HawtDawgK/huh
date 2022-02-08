package post.api.gelbooru;

import enums.PostSite;
import post.api.generic.GenericApi;

public class GelbooruApi extends GenericApi {

    @Override
    public String getBaseUrl() {
        return "https://gelbooru.com/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.GELBOORU;
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "index.php?page=autocomplete2&term=" + tags + "&type=tag_query&limit=10";
    }

}
