package pw.depixel.launcher.smjp;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;

public @Data class VersionManifest {
    private String inheritsFrom;
    private String id;
    private String time;
    private String releaseTime;
    private String type;
    private String minecraftArguments;
    private String mainClass;
    private String minimumLauncherVersion;
    private String assets;
    private String jar;
    private ArrayList<Library> libraries;

    public void setLibraries(ArrayList<Library> libraries) {
        if(inheritsFrom != null && this.libraries == null) {
            try {
                this.libraries = libraries;
                VersionManifest temp = new ObjectMapper().
                        readValue(getClass().getResourceAsStream("/" + inheritsFrom + ".json"), VersionManifest.class);
                this.libraries.addAll(temp.getLibraries());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            this.libraries = libraries;
        }
    }
}
