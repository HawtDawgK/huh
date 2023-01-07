package nsfw.post.api.hypnohub;

import nsfw.enums.PostSite;
import nsfw.post.api.generic.GenericPostApi;

public class HypnohubApi extends GenericPostApi {

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

}
