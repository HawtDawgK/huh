package post.api.xbooru;

import enums.PostSite;
import post.api.generic.GenericApi;

public class XbooruApi extends GenericApi {

    @Override
    public String getBaseUrl() {
        return "https://xbooru.com/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.XBOORU;
    }
}
