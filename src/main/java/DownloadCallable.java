import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class DownloadCallable implements Callable<List<String>> {

    private final String urlString;

    public DownloadCallable(String urlString) {
        this.urlString = urlString;
    }

    @Override
    public List<String> call() {

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

        System.out.println(Thread.currentThread().getName() + " finished downloading:" + urlString);

        return findPaths(htmlContent.toString());
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
        return paths;
    }

}