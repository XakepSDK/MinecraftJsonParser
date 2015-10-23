package pw.depixel.launcher.smjp.utils;

import org.apache.commons.lang3.SystemUtils;
import pw.depixel.launcher.smjp.minecraftjson.OSType;

public class PlatformUtils {
    public static OSType getPlatform() {
        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX) {
            return OSType.linux;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return OSType.windows;
        } else if (SystemUtils.IS_OS_MAC) {
            return OSType.osx;
        } else {
            return null;
        }
    }

    public static String getJdkArch() {
        if (System.getProperty("os.arch").contains("64")) {
            return "64";
        }
        return "32";
    }
}
