package post.api.hypnohub;

import enums.PostSite;
import post.Post;

import java.util.Date;

public class HypnohubPost extends Post {

    private Date createdAt;

    @Override
    public PostSite getSite() {
        return PostSite.HYPNOHUB;
    }
}
