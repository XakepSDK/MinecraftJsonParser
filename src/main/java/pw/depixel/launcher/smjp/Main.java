package pw.depixel.launcher.smjp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import pw.depixel.launcher.smjp.services.ICallback;
import pw.depixel.launcher.smjp.updater.DownloadCountingOutputStream;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main implements ICallback {
    private Scanner scn;
    private ObjectMapper objectMapper;
    private Versions versions;
    private VersionManifest versionManifest;
    private HashMap<ReleaseType, ArrayList<Version>> branchVersions;
    private ArrayList<Version> versionsList;
    @Getter
    private ExecutorService pool;

    public Main() {
        objectMapper = new ObjectMapper();
        pool = Executors.newFixedThreadPool(30);
        scn = new Scanner(System.in);

        try {
            launchApp();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Main();
    }

    public void launchApp() throws Exception {
        versions = objectMapper.readValue(new URL("http://s3.amazonaws.com/Minecraft.Download/versions/versions.json").openStream(), Versions.class);
        logo();
        selectBranch();
    }

    public void selectBranch() throws Exception {
        switch (scn.nextLine()) {
            case "1":
                printBranchVersions(ReleaseType.snapshot);
                break;
            case "2":
                printBranchVersions(ReleaseType.release);
                break;
            case "3":
                printBranchVersions(ReleaseType.old_beta);
                break;
            case "4":
                printBranchVersions(ReleaseType.old_alpha);
                break;
            default:
                launchApp();
                break;
        }
        scn.next();
    }

    public void printBranchVersions(ReleaseType releaseType) throws Exception {
        branchVersions = versions.getByReleaseType(releaseType);
        System.out.println("_------Доступные версии------_");

        for (ReleaseType type : branchVersions.keySet()) {
            versionsList = branchVersions.get(type);
            for (Version version : versionsList) {
                System.out.println(version.getId());
            }
        }

        System.out.println("-------Для продолжения-------");
        System.out.println("-___Введите номер версии___-");
        loadVersion(scn.nextLine());

    }

    private void loadVersion(String version) throws Exception {
        for (Version version1 : versionsList) {
            if (version1.getId().contains(version)) {
                String manifestUrl = version1.getUrl() + version1.getUrl("client.json");
                versionManifest = objectMapper.readValue(new URL(manifestUrl).openStream(), VersionManifest.class);

                ActionEvent actionEvent = new ActionEvent(this, 0, null);
                versionManifest.download(actionEvent);
                version1.download(actionEvent);

                pool.shutdown();

                break;
            }
        }
    }

    private void logo() {
        System.out.println("_------Minecraft Parser by Xakep_SDK------_");
        System.out.println("Выберите ветку:");
        System.out.println("1) Снапшоты");
        System.out.println("2) Релизы");
        System.out.println("3) Бета");
        System.out.println("4) Альфа");
        System.out.println("-_________________________________________-");
    }

    @Override
    public void completed(String isCompleted) {
        System.out.println(isCompleted);
    }

    @Override
    public void status(ActionEvent actionEvent) {
        DownloadCountingOutputStream dcos = ((DownloadCountingOutputStream) actionEvent.getSource());

        System.out.println(dcos.getFileName() + " Downloaded: " + dcos.getTotalPercentage() + "%");
    }
}
