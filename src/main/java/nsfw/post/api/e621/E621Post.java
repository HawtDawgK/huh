package nsfw.post.api.e621;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nsfw.enums.PostSite;
import nsfw.post.Post;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class E621Post implements Post {

    private long id;

    private String rating;

    private Sample sample;

    private Date createdAt;

    private Tags tags;

    private File file;

    private Score score;

    @Override
    public String getFileUrl() {
        return file.getUrl();
    }

    @Override
    public long getScore() {
        return score.getTotal();
    }

    @Override
    public String getSampleUrl() {
        return sample.getUrl();
    }

    @Override
    public String getTags() {
        return tags.getGeneral();
    }

    @Override
    public PostSite getSite() {
        return PostSite.E621;
    }

    @Override
    public Date getCreatedAt() {
        return null;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class File {
        private String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Score {
        private long total;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Sample {
        private String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Tags {
        private String general;
    }
}
