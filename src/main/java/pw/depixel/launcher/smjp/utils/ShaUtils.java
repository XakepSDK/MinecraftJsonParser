package pw.depixel.launcher.smjp.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.URL;

public class ShaUtils {
    public static boolean compare(File source, URL url) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new URL(url.toString() + ".sha1").openStream()))) {
            DigestUtils.sha1Hex(new FileInputStream(source));

            return !DigestUtils.sha1Hex(new FileInputStream(source)).equals(bufferedReader.readLine());
        } catch (IOException e) {
            //e.printStackTrace();
            return true;
        }
    }
}
