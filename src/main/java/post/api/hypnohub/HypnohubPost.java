package post.api.hypnohub;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import enums.PostSite;
import post.Post;
import util.UnixTimestampDeserializer;

import java.util.Date;

public class HypnohubPost extends Post {

    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private Date createdAt;

    @Override
    public PostSite getSite() {
        return PostSite.HYPNOHUB;
    }
}
