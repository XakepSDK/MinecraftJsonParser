package pw.depixel.launcher.smjp.updater;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import pw.depixel.launcher.smjp.minecraftjson.Extract;
import pw.depixel.launcher.smjp.services.ICallback;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

public class UnpackTask implements Runnable {
    ;
    private File filePath;
    private File unpackPath;
    private Extract extract;
    private ICallback callback;

    public UnpackTask(File filePath, File unpackPath, Extract extract, ICallback callback) {
        this.filePath = filePath;
        this.unpackPath = unpackPath;
        this.extract = extract;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            ZipFile zipFile = new ZipFile(filePath);
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                for (String string : extract.getExclude()) {
                    if (!string.equalsIgnoreCase(entry.getName() + "/")) {
                        FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry),
                                new File(unpackPath.toString() + "/" + entry.getName()));
                        System.out.println(string);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            callback.status(new ActionEvent(e, 0, null));
        }
    }
}
