package nsfw.post.api.gelbooru;

import nsfw.enums.PostSite;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.generic.GenericPostApi;

public class GelbooruApi extends GenericPostApi {

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

    @Override
    public String getUrl(PostFetchOptions options) {
        return null;
    }

}
