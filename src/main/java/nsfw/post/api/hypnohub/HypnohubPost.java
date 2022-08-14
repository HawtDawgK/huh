package nsfw.post.api.hypnohub;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import nsfw.enums.PostSite;
import nsfw.post.Post;
import nsfw.util.UnixTimestampDeserializer;

import java.util.Date;

public class HypnohubPost extends Post {

    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private Date createdAt;

    @Override
    public PostSite getSite() {
        return PostSite.HYPNOHUB;
    }
}
