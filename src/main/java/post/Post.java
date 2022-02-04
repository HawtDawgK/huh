package post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import enums.PostSite;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import util.Formats;

import java.util.Date;
import java.util.Optional;

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

    private @Nullable PostMetadata postMetadata;

    private PostSite site;

    public boolean isAnimated() {
        String[] splitUrl = fileUrl.split("\\.");

        String fileExt = splitUrl[splitUrl.length - 1];
        return Formats.isAnimated(fileExt);
    }

    public Optional<PostMetadata> getPostMetadata() {
        return Optional.ofNullable(postMetadata);
    }

    public void setPostMetadata(@Nullable PostMetadata postMetadata) {
       this.postMetadata = postMetadata;
    }
}
