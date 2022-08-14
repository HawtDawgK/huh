package nsfw.post.api.tbib;

import nsfw.enums.PostSite;
import nsfw.post.api.GenericPostApi;

public class TbibApi implements GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://tbib.org/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.TBIB;
    }
}
