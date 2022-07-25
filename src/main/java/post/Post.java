package post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import enums.PostSite;
import lombok.Getter;
import lombok.Setter;
import util.CustomDateDeserializer;
import util.Formats;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Post {

    private long id;

    private long score;

    private String fileUrl;

    private String sampleUrl;

    private String tags;

    private String rating;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date createdAt;

    private PostSite site;

    public boolean isAnimated() {
        String[] splitUrl = fileUrl.split("\\.");

        String fileExt = splitUrl[splitUrl.length - 1];
        return Formats.isAnimated(fileExt);
    }

    public PostResolvable toPostResolvable() {
        return new PostResolvable(getId(), getSite());
    }

}
