package nsfw.util;

public class TagUtil {

    private static final String[] disallowedTags = { "loli", "shota", "underage", "baby" };

    public static boolean hasDisallowedTags(String tags) {
        for (String disallowedTag : disallowedTags) {
            if (tags.contains(disallowedTag)) {
                return true;
            }
        }

        return false;
    }
}
