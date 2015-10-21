package pw.depixel.launcher.smjp;

import lombok.Data;

import java.util.HashMap;

@Data
public class Rule {
    private String action;
    private HashMap<String, OS> os;
}
