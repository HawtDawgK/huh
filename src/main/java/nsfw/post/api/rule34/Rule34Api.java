package nsfw.post.api.rule34;

import nsfw.enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.Post;
import nsfw.post.api.PostApi;
import nsfw.post.autocomplete.AutocompleteResult;

import java.util.Optional;

@Slf4j
public class Rule34Api implements PostApi<nsfw.post.api.generic.PostQueryResult<Post>, Post, AutocompleteResult> {

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
