package pw.depixel.launcher.smjp;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public @Data class Library {
    private String name;
    private String url;
    private ArrayList<Rule> rules;
    private HashMap<OS, String> natives;
    private Extract extract;

    public void setNatives(HashMap<OS, String> natives) {
        for (OS key : natives.keySet()) {
            String repl = natives.get(key);
            repl = repl.replace("${arch}", "64");
            natives.put(key, repl);
        }
        this.natives = natives;
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
}

