package nsfw.post;

import nsfw.enums.PostSite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class PostResolvable {

    private final long postId;

    private final PostSite postSite;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostResolvable that = (PostResolvable) o;
        return postId == that.postId && postSite == that.postSite;
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, postSite);
    }

}
