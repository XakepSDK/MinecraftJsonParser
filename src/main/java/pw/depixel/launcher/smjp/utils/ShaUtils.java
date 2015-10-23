package pw.depixel.launcher.smjp.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class ShaUtils {
    public static boolean compare(File source, URL url) {
        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(url.openStream()))) {

            return DigestUtils.sha1Hex(new FileInputStream(source)).equals(bufferedReader.readLine());
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }
}
