package post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import enums.PostSite;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import util.Formats;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class Post {

    private long id;

    private long score;

    private String fileUrl;

    private String sampleUrl;

    private String tags;

    private String rating;

    private Date createdAt;

    private PostSite site;

    public boolean isAnimated() {
        String[] splitUrl = fileUrl.split("\\.");

        String fileExt = splitUrl[splitUrl.length - 1];
        return Formats.isAnimated(fileExt);
    }


    public PostResolvableEntry toPostResolvableEntry() {
        return new PostResolvableEntry(getId(), getSite(), Instant.now());
    }
}
