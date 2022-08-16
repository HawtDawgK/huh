package nsfw.post.api;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PostApiException extends Exception {

    public PostApiException(String message) {
        super(message);
    }

    public PostApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
