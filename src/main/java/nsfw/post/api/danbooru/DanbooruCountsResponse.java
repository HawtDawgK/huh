package nsfw.post.api.danbooru;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import nsfw.post.api.CountResult;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DanbooruCountsResponse implements CountResult {

    private DanbooruCounts counts;

    @Override
    public int getCount() {
        return counts.getPosts();
    }
}
