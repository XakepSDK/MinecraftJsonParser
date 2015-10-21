package pw.depixel.launcher.smjp;

import com.fasterxml.jackson.databind.ObjectMapper;
import pw.depixel.launcher.smjp.updater.DownloadTask;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public Main() {
        Versions vers;
        VersionManifest jmo;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            vers = objectMapper.readValue(getClass().getResourceAsStream("/versions.json"), Versions.class);
            System.out.println(vers);
            HashMap<ReleaseType, ArrayList<Version>> list = vers.getByReleaseType(ReleaseType.snapshot);
            ExecutorService pool = Executors.newFixedThreadPool(30);
            for (ReleaseType o : list.keySet()) {
                ArrayList<Version> ver = list.get(o);
                for (Version versoins : ver) {
                    pool.submit(new DownloadTask(new URL(""), new File("")));

                    System.out.println(versoins.getId());
                }
            }
            pool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Main();
    }
}
