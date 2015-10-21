package pw.depixel.launcher.smjp;

import lombok.Data;

@Data
public class Version {
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

    public String getDownloadUrl() {
        if (url != null) {
            return url;
        }
        return "http://s3.amazonaws.com/Minecraft.Download/versions/";
    }
}