import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HTMLDownloader extends Thread {

    private final String url;
    private final String dir;
    private List<String> childDirs;

    public HTMLDownloader(String url, String dir) {
        this.url = url;
        this.dir = System.getProperty("user.dir") + dir;
    }

    @Override
    public void run() {

        File file = new File(dir);

        System.out.println("Downloading " + url);

        URL url = null;
        try {
            url = new URL(this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(url).openStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(dir + ".html"))
        ) {
            StringBuilder htmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                htmlContent.append(line);
            }
            childDirs = findChildDirs(htmlContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> findChildDirs(String htmlContent) {
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
        return childDirs;
    }

    public List<String> getChildDirs() {
        return childDirs;
    }
}