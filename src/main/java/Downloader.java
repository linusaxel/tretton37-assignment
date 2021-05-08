import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Downloader {

    private static final String TRETTON_37_BASE_URL = "https://tretton37.com";
    private static final String TARGET_DIRECTORY = "page.html";

    public static void main(String[] args) {
        int depth = 2;

        try {
            download(TRETTON_37_BASE_URL, depth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void download(String urlString, int depth) throws IOException {
        URL url = new URL(urlString);
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(TARGET_DIRECTORY));
        ) {
            StringBuilder htmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                htmlContent.append(line);
            }
            System.out.println(htmlContent);
            List<String> childDirs = findChildDirs(htmlContent.toString());
        }
        depth--;
    }

    private static List<String> findChildDirs(String htmlContent) {
        Document document = Jsoup.parse(htmlContent);
        Elements elements = document.select("a[href]");
        List<String> childDirs = new ArrayList<>();
        for (Element element: elements) {
            String href = element.attr("href");
            if (href.startsWith("/") && (href.length() > 1)) {
                childDirs.add(href);
            }
        }
        return childDirs;
    }
}