package pw.depixel.launcher.smjp;

import lombok.Data;

import java.util.HashMap;

public @Data class Rule {
    private String action;
    private HashMap<String, OS> os;
}
