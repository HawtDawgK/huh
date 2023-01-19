package nsfw.post;

import nsfw.enums.PostSite;
import nsfw.util.Formats;

import java.util.Date;

public interface Post {

    long getId();

    long getScore();

    String getFileUrl();

    String getSampleUrl();

    String getTags();

    String getRating();

    Date getCreatedAt();

    PostSite getPostSite();

    void setPostSite(PostSite postSite);

    default boolean isVideo() {
        String[] splitUrl = getFileUrl().split("\\.");

        String fileExt = splitUrl[splitUrl.length - 1];
        return Formats.isVideo(fileExt);
    }

}
