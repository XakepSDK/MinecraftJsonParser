package pw.depixel.launcher.smjp;

import com.fasterxml.jackson.databind.ObjectMapper;
import pw.depixel.launcher.smjp.updater.DownloadTask;
import pw.depixel.launcher.smjp.utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    Scanner scn;
    ObjectMapper objectMapper;
    Versions versions;
    VersionManifest versionManifest;
    HashMap<ReleaseType, ArrayList<Version>> branchVersions;
    ArrayList<Version> versionsList;
    ExecutorService pool;

    public Main() {
        objectMapper = new ObjectMapper();
        pool = Executors.newFixedThreadPool(30);
        scn = new Scanner(System.in);

        try {
            launchApp();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Main();
    }

    public void launchApp() throws IOException {
        versions = objectMapper.readValue(new URL("http://s3.amazonaws.com/Minecraft.Download/versions/versions.json").openStream(), Versions.class);
        logo();
        selectBranch();
    }

    public void selectBranch() throws IOException {
        while (scn.hasNext()) {
            if (scn.hasNextInt()) {
                switch (scn.nextInt()) {
                    case 1:
                        printBranchVersions(ReleaseType.snapshot);
                        break;
                    case 2:
                        printBranchVersions(ReleaseType.release);
                        break;
                    case 3:
                        printBranchVersions(ReleaseType.old_beta);
                        break;
                    case 4:
                        printBranchVersions(ReleaseType.old_alpha);
                        break;
                    default:
                        launchApp();
                        break;
                }
                break;
            } else {
                scn.next();
            }
        }
    }

    public void printBranchVersions(ReleaseType releaseType) throws IOException {
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
        selectVersion();

    }

    public void selectVersion() throws IOException {
        while (scn.hasNext()) {
            if (scn.hasNextLine()) {
                loadVersion(scn.nextLine());
                break;
            } else {
                scn.next();
            }
        }
    }

    private void loadVersion(String version) throws IOException {
        for (Version version1 : versionsList) {
            if (version1.getId().contains(version)) {
                String manifestUrl = version1.getUrl() + version1.getUrl("client.json");
                versionManifest = objectMapper.readValue(new URL(manifestUrl).openStream(), VersionManifest.class);
                loadAll(version1);
                break;
            }
        }
    }

    private void loadAll(Version version1) throws IOException {
        ArrayList<Library> libs = versionManifest.getLibraries();
        for (Library lib : libs) {
            if (lib.getNatives() != null) {
                HashMap<OS, String> natives = lib.getNatives();
                for (OS o : natives.keySet()) {
                    pool.submit(new DownloadTask(new URL(lib.getUrl() + lib.getArtifactPath(natives.get(o))),
                            new File(PathUtils.getWorkingDirectory() + "libraries/" + lib.getArtifactPath(natives.get(o)))));
                }
            } else {
                pool.submit(new DownloadTask(new URL(lib.getUrl() + lib.getArtifactPath()),
                        new File(PathUtils.getWorkingDirectory() + "libraries/" + lib.getArtifactPath())));
            }
        }
        String manifestPath = version1.getUrl("client.json");
        String manifestUrl = version1.getUrl() + manifestPath;
        String jarPath = version1.getUrl("client.jar");
        String jarUrl = version1.getUrl() + jarPath;

        pool.submit(new DownloadTask(new URL(manifestUrl), new File(PathUtils.getWorkingDirectory() + "versions/" + manifestPath)));
        pool.submit(new DownloadTask(new URL(jarUrl), new File(PathUtils.getWorkingDirectory() + "versions/" + jarPath)));
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
}
