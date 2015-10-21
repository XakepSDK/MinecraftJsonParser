package pw.depixel.launcher.smjp;

import com.fasterxml.jackson.databind.ObjectMapper;
import pw.depixel.launcher.smjp.updater.ChunkedDownloader;
import pw.depixel.launcher.smjp.utils.PathUtils;
import pw.depixel.launcher.smjp.utils.ShaUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
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
        loadVersion(scn.nextLine());

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
        TreeMap<URL, File> downloadList = new TreeMap<>((o1, o2) -> {
            return o1.toString().compareTo(o2.toString());
        });

        URL url;
        File savePath;
        StringBuilder sb = new StringBuilder();
        for (Library lib : libs) {
            if (lib.getNatives() != null) {
                HashMap<OSType, String> natives = lib.getNatives();
                for (OSType os : natives.keySet()) {
                    String classifier = natives.get(os);

                    url = new URL(lib.getUrl() + lib.getArtifactPath(classifier));
                    savePath = new File(PathUtils.getWorkingDirectory("libraries/") + lib.getArtifactPath(classifier));

                    if (ShaUtils.compare(savePath, url)) {
                        downloadList.put(url, savePath);
                    }
                }
            } else {
                url = new URL(lib.getUrl() + lib.getArtifactPath());
                savePath = new File(PathUtils.getWorkingDirectory("libraries/") + lib.getArtifactPath());

                sb.append("libraries/").append(lib.getArtifactPath()).append(";");

                if (ShaUtils.compare(savePath, url)) {
                    downloadList.put(url, savePath);
                }
            }
        }
        System.out.println(sb);

        String manifestPath = version1.getUrl("client.json");
        String manifestUrl = version1.getUrl() + manifestPath;
        String jarPath = version1.getUrl("client.jar");
        String jarUrl = version1.getUrl() + jarPath;

        url = new URL(manifestUrl);
        savePath = new File(PathUtils.getWorkingDirectory("versions/") + manifestPath);
        if (ShaUtils.compare(savePath, url)) {
            downloadList.put(url, savePath);
        }

        url = new URL(jarUrl);
        savePath = new File(PathUtils.getWorkingDirectory("versions/") + jarPath);
        if (ShaUtils.compare(savePath, url)) {
            downloadList.put(url, savePath);
        }

        ChunkedDownloader downloader = new ChunkedDownloader(pool);
        downloader.addToQueue(downloadList);
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
