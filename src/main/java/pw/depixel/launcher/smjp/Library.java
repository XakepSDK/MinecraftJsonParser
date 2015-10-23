package pw.depixel.launcher.smjp;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pw.depixel.launcher.smjp.services.IDownloadable;
import pw.depixel.launcher.smjp.updater.DownloadTask;
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
public class Library implements IDownloadable {
    private String name;
    private String url;
    private ArrayList<Rule> rules;
    private HashMap<OSType, String> natives;
    private Extract extract;

    private boolean download = false;
    private OSType osType;
    private String nativeClassifier;

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

        Main main = ((Main) actionEvent.getSource());
        ExecutorService pool = main.getPool();

        if (download && !checkSha1()) {
            if (osType != null && nativeClassifier != null) {
                pool.submit(new DownloadTask(new URL(getUrl() + getArtifactPath(nativeClassifier)),
                        PathUtils.getWorkingDirectory("libraries/") + getArtifactPath(nativeClassifier), main));
                main.completed(getArtifactFilename() + " Downloaded.");
            } else {
                pool.submit(new DownloadTask(new URL(getUrl() + getArtifactPath()),
                        PathUtils.getWorkingDirectory("libraries/") + getArtifactPath(), main));
                main.completed(getArtifactFilename() + " Downloaded.");
            }
        } else {
            main.completed(getArtifactFilename() + " not allowed to download.");
        }
    }

    private boolean checkSha1() throws MalformedURLException {
        return ShaUtils.compare(new File(PathUtils.getWorkingDirectory("libraries/") + getArtifactPath()),
                new URL(getUrl() + getArtifactPath() + ".sha1"));
    }
}