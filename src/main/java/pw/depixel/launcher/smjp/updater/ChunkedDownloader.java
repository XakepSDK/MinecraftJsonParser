package pw.depixel.launcher.smjp.updater;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;

public class ChunkedDownloader {
    private ExecutorService pool;

    public ChunkedDownloader(ExecutorService pool) {
        this.pool = pool;
    }

    public void addToQueue(TreeMap<URL, File> fileList) throws IOException {
        ArrayList<URL> list = new ArrayList<>();
        list.addAll(fileList.keySet());

        System.out.println("ARRAY SIZE: " + fileList.size());
        int arrSize = list.size();

        for (int start = 0; start < arrSize; start += 5) {
            int end = Math.min(start + 5, arrSize);

            System.out.println("START: " + start + " END: " + end);
            if (end == arrSize) end--;

            pool.submit(new DownloadTask(fileList.subMap(list.get(start), list.get(end))));
        }
        pool.shutdown();
    }
}
