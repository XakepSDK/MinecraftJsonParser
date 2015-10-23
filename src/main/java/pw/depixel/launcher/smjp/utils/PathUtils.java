package pw.depixel.launcher.smjp.utils;

import java.io.File;

public class PathUtils {
    public static String getWorkingDirectory() {
        return "D:/Minecraft/";
    }

    public static String getWorkingDirectory(String classifier) {
        File path = new File(getWorkingDirectory() + classifier);
        if (!path.exists())
            path.mkdirs();

        return getWorkingDirectory() + classifier;
    }
}
