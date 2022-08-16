package nsfw.post.api.rule34;

import nsfw.enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.GenericPostApi;

import java.util.Optional;

@Slf4j
public class Rule34Api extends GenericPostApi {

    @Override
    public String getBaseUrl() {
        return "https://api.rule34.xxx/";
    }

    @Override
    public PostSite getSite() {
        return PostSite.RULE34;
    }

    @Override
    public Optional<Integer> getMaxCount() {
        return Optional.of(200000);
    }

}
