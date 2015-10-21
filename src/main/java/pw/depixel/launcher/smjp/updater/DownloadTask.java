package pw.depixel.launcher.smjp.updater;

import org.apache.commons.compress.utils.IOUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;

public class DownloadTask implements Runnable, ActionListener {

    private final URL url;
    private final File saveFile;

    long total;

    public DownloadTask(URL url, File saveFile) throws IOException {
        this.url = url;
        this.saveFile = saveFile;

        File parentPath = saveFile.getParentFile();
        if (!parentPath.exists() && !parentPath.mkdirs())
            throw new IOException("Can't create path!");
    }

    @Override
    public void run() {
        try (OutputStream os = new FileOutputStream(saveFile);
             InputStream is = url.openStream()) {

            total = Long.parseLong(url.openConnection().getHeaderField("Content-Length"));
            DownloadCountingOutputStream dcount = new DownloadCountingOutputStream(os, total, this);
            IOUtils.copy(is, dcount);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Total in % = (Downloaded * 100) / Total
        System.out.println(url + " Downloaded: " + ((DownloadCountingOutputStream) e.getSource()).getTotalInPercents());
    }
}
