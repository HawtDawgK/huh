package nsfw.post.api.tbib;

import nsfw.post.api.generic.GenericPostApi;

public class TbibApi extends GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://tbib.org/";
    }

}
