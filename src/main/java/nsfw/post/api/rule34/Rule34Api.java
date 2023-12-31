package nsfw.post.api.rule34;

import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.generic.GenericPostApi;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
public class Rule34Api extends GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://api.rule34.xxx";
    }

    @Override
    public Optional<Integer> getMaxCount() {
        return Optional.of(200000);
    }

    @Override
    public String getUrl(PostFetchOptions options) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(getBaseUrl());

        uriComponentsBuilder.queryParam("page", "dapi");
        uriComponentsBuilder.queryParam("s", "post");
        uriComponentsBuilder.queryParam("q", "index");
        uriComponentsBuilder.queryParam("limit", 1);

        String tags = "";

        if (options.getId() != null) {
            tags += "id:" + options.getId();
        } else if (options.getTags() != null) {
            tags += options.getTags();
        }

        if (!tags.isEmpty()) {
            uriComponentsBuilder.queryParam("tags", tags);
        }

        if (options.getPage() != null) {
            uriComponentsBuilder.queryParam("pid", options.getPage());
        }

        return uriComponentsBuilder.toUriString();
    }

}
