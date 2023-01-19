package nsfw.post;

public record PostFetchResult(Post post, boolean isError, String message) {

}
