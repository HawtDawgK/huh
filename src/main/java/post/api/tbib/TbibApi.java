package post.api.tbib;

import enums.PostSite;
import post.api.generic.GenericApi;

public class TbibApi extends GenericApi {

    @Override
    public String getBaseUrl() {
        return "https://tbib.org/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.TBIB;
    }
}
