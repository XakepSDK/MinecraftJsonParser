package pw.depixel.launcher.smjp.minecraftjson;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pw.depixel.launcher.smjp.Main;
import pw.depixel.launcher.smjp.services.ICallback;
import pw.depixel.launcher.smjp.services.IDownloadable;
import pw.depixel.launcher.smjp.services.IUnpackable;
import pw.depixel.launcher.smjp.updater.DownloadTask;
import pw.depixel.launcher.smjp.updater.UnpackTask;
import pw.depixel.launcher.smjp.utils.PathUtils;
import pw.depixel.launcher.smjp.utils.PlatformUtils;
import pw.depixel.launcher.smjp.utils.ShaUtils;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

@Data
public class Library implements IDownloadable, IUnpackable, ICallback {
    private String name;
    private String url;
    private ArrayList<Rule> rules;
    private HashMap<OSType, String> natives;
    private Extract extract;

    private boolean download = false;
    private OSType osType;
    private String nativeClassifier;
    private ICallback callback;
    private ActionEvent actionEvent;
    private Main main;

    public void setNatives(HashMap<OSType, String> natives) {
        for (OSType key : natives.keySet()) {
            String repl = natives.get(key);
            repl = repl.replace("${arch}", PlatformUtils.getJdkArch());
            natives.put(key, repl);
            if (key == PlatformUtils.getPlatform()) {
                osType = key;
                nativeClassifier = natives.get(key);
            }
        }
        this.natives = natives;
    }

    public void setRules(ArrayList<Rule> rules) {
        if (rules != null) {
            for (Rule rule : rules) {
                switch (rule.getAction()) {
                    case allow:
                        download = rule.getOs() == null || rule.getOs().getName() == PlatformUtils.getPlatform();
                        break;
                    case disallow:
                        download = rule.getOs() != null && rule.getOs().getName() != PlatformUtils.getPlatform();
                        break;
                    default:
                        download = false;
                        break;
                }
            }
        }
        this.rules = rules;
    }

    public String getArtifactBaseDir() {
        if (name == null) {
            throw new IllegalStateException("Cannot get artifact dir of empty/blank artifact");
        }
        String[] parts = name.split(":", 3);
        return String.format("%s/%s/%s", parts[0].replaceAll("\\.", "/"), parts[1], parts[2]);
    }

    public String getArtifactPath() {
        return getArtifactPath(null);
    }

    public String getArtifactPath(String classifier) {
        if (name == null) {
            throw new IllegalStateException("Cannot get artifact path of empty/blank artifact");
        }
        return String.format("%s/%s", getArtifactBaseDir(), getArtifactFilename(classifier));
    }

    public String getArtifactFilename() {
        return getArtifactFilename(null);
    }

    public String getArtifactFilename(String classifier) {
        if (name == null) {
            throw new IllegalStateException("Cannot get artifact filename of empty/blank artifact");
        }
        String[] parts = name.split(":", 3);
        Object[] arrobject = new Object[3];
        arrobject[0] = parts[1];
        arrobject[1] = parts[2];
        arrobject[2] = StringUtils.isEmpty(classifier) ? "" : "-" + classifier;
        return String.format("%s-%s%s.jar", arrobject);
    }

    public String getUrl() {
        if (url != null) {
            return url;
        }
        return "https://libraries.minecraft.net/";
    }

    @Override
    public void download(ActionEvent actionEvent) throws MalformedURLException {
        checkSha1();

        main = ((Main) actionEvent.getSource());
        this.actionEvent = actionEvent;
        ExecutorService pool = main.getPool();
        callback = main;

        if (download && !checkSha1()) {
            if (osType != null && nativeClassifier != null) {
                pool.submit(new DownloadTask(
                        new URL(getUrl() + getArtifactPath(nativeClassifier)),
                        new File(PathUtils.getWorkingDirectory("libraries/") + getArtifactPath(nativeClassifier)),
                        this));
            } else {
                pool.submit(new DownloadTask(
                        new URL(getUrl() + getArtifactPath()),
                        new File(PathUtils.getWorkingDirectory("libraries/") + getArtifactPath()),
                        this));
            }
        } else {
            callback.completed(getArtifactFilename() + " not allowed to download.");
        }
    }

    private boolean checkSha1() throws MalformedURLException {
        return ShaUtils.compare(new File(PathUtils.getWorkingDirectory("libraries/") + getArtifactPath()),
                new URL(getUrl() + getArtifactPath() + ".sha1"));
    }

    @Override
    public void unpack(File path, File unpackPath) {
        ExecutorService pool = main.getPool();
        pool.submit(new UnpackTask(path, unpackPath, extract, callback));
    }

    @Override
    public void completed(String isCompleted) {
        if (osType != null && nativeClassifier != null) {
            unpack(new File(PathUtils.getWorkingDirectory("libraries/") + getArtifactPath(nativeClassifier)),
                    new File(PathUtils.getWorkingDirectory("versions/" + main.getVersionManifest().getId() + "/natives/")));
        }
        callback.completed(isCompleted + getArtifactFilename(nativeClassifier));
    }

    @Override
    public void status(ActionEvent actionEvent) {
        callback.status(actionEvent);
    }
}