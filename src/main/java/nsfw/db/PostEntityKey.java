package nsfw.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import nsfw.enums.PostSite;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PostEntityKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private long userId;

    private long postId;

    private PostSite siteName;
}
