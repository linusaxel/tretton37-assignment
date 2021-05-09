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
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DownloadCallable implements Callable<ConcurrentHashMap<String, Boolean>> {

    private final String urlString;
    private final ConcurrentHashMap<String, Boolean> pathsMap;

    public DownloadCallable(ConcurrentHashMap<String, Boolean> pathsMap, String urlString) {
        this.pathsMap = pathsMap;
        this.urlString = urlString;
    }

    @Override
    public ConcurrentHashMap<String, Boolean> call() {

        System.out.println(Thread.currentThread().getName() + " downloading:" + urlString);

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL");
        }

        try {
            Files.createDirectories(Paths.get(url.getHost() + url.getPath()));
        } catch (IOException e) {
            System.out.println("Couldn't create directory: " + url.getHost() + url.getPath());
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
            System.out.println("Couldn't download from: " + urlString);
        }

        //Update pathsMap
        List<String> paths = findPaths(htmlContent.toString());
        for (String path : paths) {
            if (!pathsMap.containsKey(path)) {
                pathsMap.put(url.getProtocol() + "://" +  url.getHost() + path, false);
            }
        }

        pathsMap.put(urlString, true);
        System.out.println(Thread.currentThread().getName() + " finished downloading:" + urlString);
        return pathsMap;
    }

    public static List<String> findPaths(String htmlContent) {
        Document document = Jsoup.parse(htmlContent);
        Elements elements = document.select("a[href]");
        List<String> paths = new ArrayList<>();
        for (Element element: elements) {
            String href = element.attr("href");
            if (href.startsWith("/") && (href.length() > 1)) {
                paths.add(href);
            }
        }
        paths = paths.stream().distinct().collect(Collectors.toList());
        System.out.println("Found paths: " + Arrays.toString(paths.toArray()));
        return paths;
    }

}