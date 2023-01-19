package nsfw.post.api.xbooru;

import nsfw.enums.PostSite;
import nsfw.post.api.generic.GenericPostApi;

public class XbooruApi extends GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://xbooru.com/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.XBOORU;
    }

}
