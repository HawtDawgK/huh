package post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PostMetadata {

    private final long tagCount;

    private final int page;
}
