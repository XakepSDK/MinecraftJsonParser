package pw.depixel.launcher.smjp.utils;

import pw.depixel.launcher.smjp.Rule;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Unpacker implements Runnable {

    private HashMap<File, ArrayList<Rule>> natives;
    private String unpackPath;

    public Unpacker(HashMap<File, ArrayList<Rule>> natives, String unpackPath) {
        this.natives = natives;
        this.unpackPath = unpackPath;
    }

    @Override
    public void run() {

    }
}
