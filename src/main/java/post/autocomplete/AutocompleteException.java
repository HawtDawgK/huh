package post.autocomplete;

public class AutocompleteException extends Exception {
    public AutocompleteException(String message) {
        super(message);
    }

    public AutocompleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
