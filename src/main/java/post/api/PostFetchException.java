package post.api;

public class PostFetchException  extends Exception {

    public PostFetchException(String message) {
        super(message);
    }

    public PostFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
