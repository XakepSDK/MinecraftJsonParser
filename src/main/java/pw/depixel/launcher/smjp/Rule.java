package pw.depixel.launcher.smjp;

import lombok.Data;

@Data
public class Rule {
    private String action;
    private OS os;
    private String version;
}

@Data
class OS {
    private OSType name;
    private String version;
}