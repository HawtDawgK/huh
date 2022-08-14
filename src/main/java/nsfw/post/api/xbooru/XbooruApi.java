package nsfw.post.api.xbooru;

import nsfw.enums.PostSite;
import nsfw.post.api.GenericPostApi;

public class XbooruApi implements GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://xbooru.com/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.XBOORU;
    }
}
