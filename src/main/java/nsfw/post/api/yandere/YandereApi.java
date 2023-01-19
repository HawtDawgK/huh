package nsfw.post.api.yandere;

import nsfw.enums.PostSite;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.generic.GenericPostApi;
import org.springframework.web.util.UriComponentsBuilder;

public class YandereApi extends GenericPostApi  {

    @Override
    public String getBaseUrl() {
        return "https://yande.re";
    }

    @Override
    public PostSite getSite() {
        return PostSite.YANDERE;
    }

    @Override
    public String getUrl(PostFetchOptions options) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getBaseUrl());
        builder.path("post.xml");

        builder.queryParam("limit", "1");

        Long id = options.getId();

        if (id != null) {
            builder.queryParam("tags", "id:" + id);
        } else {
            if (options.getPage() != null) {
                builder.queryParam("page", options.getPage());
            }
            if (options.getTags() != null) {
                builder.queryParam("tags", options.getTags());
            }
        }

        return builder.toUriString();
    }
}
