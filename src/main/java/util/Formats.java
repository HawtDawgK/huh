package util;

import java.util.List;

public class Formats {

    public static final List<String> ANIMATED_FORMATS = List.of("mp4", "mov", "avi");

    public static boolean isAnimated(String fileExt) {
        return ANIMATED_FORMATS.stream()
                .anyMatch(fmt -> fmt.equalsIgnoreCase(fileExt));
    }
}
