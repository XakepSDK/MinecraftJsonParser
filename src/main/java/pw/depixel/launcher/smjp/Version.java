package pw.depixel.launcher.smjp;

import lombok.Data;
import pw.depixel.launcher.smjp.services.IDownloadable;
import pw.depixel.launcher.smjp.updater.DownloadTask;
import pw.depixel.launcher.smjp.utils.PathUtils;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;

@Data
public class Version implements IDownloadable {
    private String id;
    private String time;
    private String releaseTime;
    private ReleaseType type;
    private String url;

    public String getUrl(String type) {

        switch (type) {
            case "client.json":
                return id + "/" + id + ".json";
            case "client.jar":
                return id + "/" + id + ".jar";
            case "server.jar":
                return id + "/" + "minecraft_server." + id + ".jar";
            case "server.exe":
                return id + "/" + "minecraft_server." + id + ".exe";
            default:
                throw new IllegalStateException("Illegal type. Only client.json, client.jar, server.jar, server.exe allowed!");
        }
    }

    public String getUrl() {
        if (url != null) {
            return url;
        }
        return "http://s3.amazonaws.com/Minecraft.Download/versions/";
    }

    @Override
    public void download(ActionEvent actionEvent) throws MalformedURLException {
        //TODO: Rewrite

        String json_slink = PathUtils.getWorkingDirectory("versions/") + getUrl("client.json");
        String json_dlink = getUrl() + getUrl("client.jar");

        String jar_slink = PathUtils.getWorkingDirectory("versions/") + getUrl("client.json");
        String jar_dlink = getUrl() + getUrl("client.jar");

        Main main = ((Main) actionEvent.getSource());
        ExecutorService pool = main.getPool();

        new File(json_slink).getParentFile().mkdirs();
        new File(jar_slink).getParentFile().mkdirs();

        pool.submit(new DownloadTask(new URL(json_dlink), json_slink, main));
        pool.submit(new DownloadTask(new URL(jar_dlink), jar_slink, main));
    }
}