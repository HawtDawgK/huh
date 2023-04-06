package nsfw.post.api.yandere;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import nsfw.enums.PostSite;
import nsfw.post.Post;
import nsfw.util.UnixTimestampDeserializer;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class YanderePost implements Post {

    private long id;

    private long score;

    private String fileUrl;

    private String sampleUrl;

    private String tags;

    private String rating;

    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private Date createdAt;

    private PostSite postSite;
}
