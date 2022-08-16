package nsfw.post.api;

public class PostApiException extends Exception {

    public PostApiException(String message) {
        super(message);
    }

    public PostApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
