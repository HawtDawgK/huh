package nsfw.util;

import java.util.ArrayList;
import java.util.List;

public class TagUtil {

    private static final String[] disallowedTags = {"loli", "shota", "underage", "baby"};

    public static List<String> getDisallowedTags(String tags) {
        List<String> disallowedTagsFound = new ArrayList<>();

        for (String disallowedTag : disallowedTags) {
            if (tags.contains(disallowedTag)) {
                disallowedTagsFound.add(disallowedTag);
            }
        }

        return disallowedTagsFound;
    }
}
