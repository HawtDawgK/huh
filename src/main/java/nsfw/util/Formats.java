package nsfw.util;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Formats {

    public static final List<String> ANIMATED_FORMATS = List.of("mp4", "mov", "avi");

    public static boolean isVideo(String fileExt) {
        return ANIMATED_FORMATS.stream().anyMatch(fmt -> fmt.equalsIgnoreCase(fileExt));
    }
}
