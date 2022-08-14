package nsfw.post.api.danbooru;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import nsfw.enums.PostSite;
import lombok.Getter;
import nsfw.post.Post;

@Getter()
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DanbooruPost extends Post {

    @Override
    public PostSite getSite() {
        return PostSite.DANBOORU;
    }
}
