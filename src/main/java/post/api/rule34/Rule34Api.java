package post.api.rule34;

import enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import post.api.generic.GenericApi;

import java.util.Optional;

@Slf4j
public class Rule34Api extends GenericApi {

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
