package pw.depixel.launcher.smjp.updater;

import org.apache.commons.compress.utils.IOUtils;
import pw.depixel.launcher.smjp.services.ICallback;
import sun.plugin.dom.exception.InvalidStateException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.SortedMap;

public class DownloadTask implements Runnable, ActionListener {

    private SortedMap<URL, File> urlFileSortedMap;
    private File currentFile;
    private long total;
    private ICallback callback;

    public DownloadTask(SortedMap<URL, File> urlFileSortedMap, ICallback callback) {
        this.urlFileSortedMap = urlFileSortedMap;
        this.callback = callback;
    }

    @Override
    public void run() {
        for (URL url : urlFileSortedMap.keySet()) {
            currentFile = urlFileSortedMap.get(url);

            File parentPath = currentFile.getParentFile();
            if (!parentPath.exists() && !parentPath.mkdirs())
                throw new InvalidStateException("Can't create path!");

            try (OutputStream os = new FileOutputStream(currentFile);
                 InputStream is = url.openStream()) {

                total = Long.parseLong(url.openConnection().getHeaderField("Content-Length"));
                DownloadCountingOutputStream dcount = new DownloadCountingOutputStream(os, total, this);
                IOUtils.copy(is, dcount);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        callback.callback();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Total in % = (Downloaded * 100) / Total
        System.out.println(currentFile.getName() + " Downloaded: " + ((DownloadCountingOutputStream) e.getSource()).getTotalInPercents());
    }
}
