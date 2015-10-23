package pw.depixel.launcher.smjp.minecraftjson;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Data
public class Versions {
    private Latest latest;
    private ArrayList<Version> versions;

    public HashMap<ReleaseType, ArrayList<Version>> getByReleaseType(ReleaseType... releaseTypes) {
        HashMap<ReleaseType, ArrayList<Version>> returnMap = new HashMap<>();

        for (ReleaseType type : releaseTypes) {
            ArrayList<Version> currentVersList = versions.stream().filter(version -> version.getType().equals(type))
                    .collect(Collectors.toCollection(ArrayList::new));
            returnMap.put(type, currentVersList);
        }
        return returnMap;
    }
}

@Data
class Latest {
    private String snapshot;
    private String release;
}