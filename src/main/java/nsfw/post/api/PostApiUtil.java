package nsfw.post.api;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class PostApiUtil {

    public static String encodeSpaces(String tags) {
        return tags.replace(' ', '+');
    }

    public static String getLastAutocompleteString(String autocompleteInput) {
        String[] parts = autocompleteInput.split(" ");

        if (parts.length == 0) {
            return "";
        }

        return parts[parts.length - 1];
    }

}
