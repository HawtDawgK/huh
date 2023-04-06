package nsfw.post.api.danbooru;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import nsfw.post.api.generic.GenericPost;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DanbooruPost extends GenericPost {

    @JsonDeserialize
    private Date createdAt;

    private String tagString;

    @Override
    public String getTags() {
        return tagString;
    }
}
