package pw.depixel.launcher.smjp;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import pw.depixel.launcher.smjp.services.IDownloadable;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

@Data
public class VersionManifest implements IDownloadable {
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
        if (inheritsFrom != null && this.libraries == null) {
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

    @Override
    public void download(ActionEvent actionEvent) throws Exception {
        for (Library lib : libraries) {
            lib.download(actionEvent);
        }
    }
}
