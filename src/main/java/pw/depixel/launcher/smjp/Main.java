package pw.depixel.launcher.smjp;

import com.fasterxml.jackson.databind.ObjectMapper;
import pw.depixel.launcher.smjp.updater.DownloadTask;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public Main() {
        VersionManifest jmo;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            jmo = objectMapper.readValue(getClass().getResourceAsStream("/1.8-forge.json"), VersionManifest.class);

            ArrayList<Library> libs = jmo.getLibraries();

            ExecutorService pool = Executors.newFixedThreadPool(30);

            // TODO: Переписать эту часть.
            for (Library lib : libs) {
                if(lib.getNatives() != null) {
                    HashMap<OS, String> natives = lib.getNatives();
                    for (OS o : natives.keySet()) {
                        pool.submit(new DownloadTask(new URL(lib.getUrl() + lib.getArtifactPath(natives.get(o))), "D:/libs/" + lib.getArtifactPath(natives.get(o))));
                        /*ZipFile test = new ZipFile(new File("D:/libs/" + lib.getArtifactPath(natives.get(o))));
                        Enumeration<ZipArchiveEntry> entries = test.getEntries();
                        while(entries.hasMoreElements()) {
                            System.out.println(entries.nextElement().getName());
                        }*/
                    }
                } else {
                    pool.submit(new DownloadTask(new URL(lib.getUrl() + lib.getArtifactPath()), "D:/libs/" + lib.getArtifactPath()));
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
