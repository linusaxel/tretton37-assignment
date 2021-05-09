import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HTMLDownloader extends Thread {

    private final String urlString;
    private final DownloadedDirs downloadedDirs;

    public HTMLDownloader(DownloadedDirs downloadedDirs, String urlString) {
        this.urlString = urlString;
        this.downloadedDirs = downloadedDirs;
    }

    @Override
    public void run() {

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        System.out.println("Downloading page: " + url);

        try {
            Files.createDirectories(Paths.get(url.getHost() + url.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder htmlContent = new StringBuilder();
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(url).openStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(url.getHost() + url.getPath() + "/index.html"))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                htmlContent.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> paths = findPaths(htmlContent.toString());
        for (String path : paths) {
            if (!downloadedDirs.getDirs().containsKey(path)) {
                downloadedDirs.addDir(url.getProtocol() + "://" +  url.getHost() + path, false);
            }
        }

        downloadedDirs.markAsDownloaded(urlString);
    }

    public static List<String> findPaths(String htmlContent) {
        Document document = Jsoup.parse(htmlContent);
        Elements elements = document.select("a[href]");
        List<String> childDirs = new ArrayList<>();
        for (Element element: elements) {
            String href = element.attr("href");
            if (href.startsWith("/") && (href.length() > 1)) {
                childDirs.add(href);
            }
        }
        childDirs = childDirs.stream().distinct().collect(Collectors.toList());
        System.out.println("Found child dirs: " + Arrays.toString(childDirs.toArray()));
        return childDirs;
    }

}