package pw.depixel.launcher.smjp;

import lombok.Data;

@Data
public class Rule {
    private Action action;
    private OS os;
    private String version;

    enum Action {
        allow, disallow
    }
}

@Data
class OS {
    private OSType name;
    private String version;
}