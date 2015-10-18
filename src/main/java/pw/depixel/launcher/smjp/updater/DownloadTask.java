package pw.depixel.launcher.smjp.updater;

import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public @Data class DownloadTask implements Runnable {

    private final String url;
    private final String savePath;

    @Override
    public void run() {
        try {
            FileUtils.copyURLToFile(new URL(url), new File(savePath));
        } catch (IOException e) {
            System.out.println(url);
        }
    }
}
