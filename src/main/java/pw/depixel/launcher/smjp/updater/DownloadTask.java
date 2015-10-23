package pw.depixel.launcher.smjp.updater;

import org.apache.commons.compress.utils.IOUtils;
import pw.depixel.launcher.smjp.services.ICallback;

import java.io.*;
import java.net.URL;

public class DownloadTask implements Runnable {

    private final URL url;
    private final File savePath;
    private final ICallback callback;

    private long total;

    public DownloadTask(URL url, File savePath, ICallback callback) {
        if (!savePath.exists()) {
            if (!savePath.getParentFile().mkdirs())
                System.out.println("Error! Directory is not created.");
        }

        this.url = url;
        this.savePath = savePath;
        this.callback = callback;
    }

    @Override
    public void run() {
        try (OutputStream os = new FileOutputStream(savePath);
             InputStream is = url.openStream()) {

            total = Long.parseLong(url.openConnection().getHeaderField("Content-Length"));
            IOUtils.copy(is, new DownloadCountingOutputStream(os, total, callback));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}