package pw.depixel.launcher.smjp.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.URL;

public class ShaUtils {
    public static boolean compare(File source, URL url) {
        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(url.openStream()))) {

            if (!source.exists()) {
                source.getParentFile().mkdirs();
                return false;
            }

            return DigestUtils.sha1Hex(new FileInputStream(source)).equals(bufferedReader.readLine());
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
    }
}
