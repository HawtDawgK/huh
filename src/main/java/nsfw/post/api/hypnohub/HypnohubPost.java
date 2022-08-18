package nsfw.post.api.hypnohub;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import nsfw.enums.PostSite;
import nsfw.post.api.generic.GenericPost;
import nsfw.util.UnixTimestampDeserializer;

import java.util.Date;

public class HypnohubPost extends GenericPost {

    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private Date createdAt;

    @Override
    public PostSite getSite() {
        return PostSite.HYPNOHUB;
    }
}
