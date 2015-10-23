package pw.depixel.launcher.smjp.updater;

import lombok.Data;
import org.apache.commons.compress.utils.IOUtils;
import pw.depixel.launcher.smjp.services.ICallback;

import java.io.*;
import java.net.URL;

@Data
public class DownloadTask implements Runnable {

    private final URL url;
    private final String savePath;

    private final ICallback callback;

    long total;

    @Override
    public void run() {
        try (OutputStream os = new FileOutputStream(new File(savePath));
             InputStream is = url.openStream()) {

            total = Long.parseLong(url.openConnection().getHeaderField("Content-Length"));
            DownloadCountingOutputStream dcount = new DownloadCountingOutputStream(os, total, callback, new File(savePath).getName());
            IOUtils.copy(is, dcount);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}