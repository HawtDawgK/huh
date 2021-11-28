package post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import util.CustomDateDeserializer;
import util.Formats;

import java.util.Date;
import java.util.Optional;

@Getter()
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

    private @Nullable PostMetadata postMetadata;

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
