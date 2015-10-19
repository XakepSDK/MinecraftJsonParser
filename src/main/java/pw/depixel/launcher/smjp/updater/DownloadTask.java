package pw.depixel.launcher.smjp.updater;

import lombok.Data;
import org.apache.commons.compress.utils.IOUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;

public
@Data
class DownloadTask implements Runnable, ActionListener {

    private final URL url;
    private final String savePath;

    long total;

    @Override
    public void run() {
        try (OutputStream os = new FileOutputStream(new File(savePath));
             InputStream is = url.openStream()) {

            total = Long.parseLong(url.openConnection().getHeaderField("Content-Length"));
            DownloadCountingOutputStream dcount = new DownloadCountingOutputStream(os, total, this);
            IOUtils.copy(is, dcount);

        } catch (IOException e) {
            System.out.println(url);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Total in % = (Downloaded * 100) / Total
        System.out.println(url + " Downloaded: " + ((DownloadCountingOutputStream) e.getSource()).getTotalInPercents());
    }
}
