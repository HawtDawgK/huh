package nsfw.post.api.tbib;

import nsfw.enums.PostSite;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.generic.GenericPostApi;

public class TbibApi extends GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://tbib.org/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.TBIB;
    }

}
