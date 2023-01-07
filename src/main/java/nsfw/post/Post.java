package nsfw.post;

import nsfw.enums.PostSite;
import nsfw.util.Formats;

import java.util.Date;

public interface Post {

    default PostResolvable toPostResolvable(PostSite postSite) {
        return new PostResolvable(getId(), postSite);
    }

    long getId();

    long getScore();

    String getFileUrl();

    String getSampleUrl();

    String getTags();

    String getRating();

    Date getCreatedAt();

    default boolean isVideo() {
        String[] splitUrl = getFileUrl().split("\\.");

        String fileExt = splitUrl[splitUrl.length - 1];
        return Formats.isVideo(fileExt);
    }

}
