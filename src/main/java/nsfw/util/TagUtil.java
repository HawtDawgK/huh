package nsfw.util;

import java.util.Arrays;
import java.util.List;

public class TagUtil {

    private static final String[] disallowedTags = {"loli", "shota", "underage", "baby"};

    public static List<String> getDisallowedTags(String tags) {
        return Arrays.stream(disallowedTags).
                filter(tags::contains)
                .toList();
    }
}
