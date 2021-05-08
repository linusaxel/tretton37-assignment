import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class Downloader {

    private static final String TRETTON_37_BASE_URL = "https://tretton37.com";
    private static final String TARGET_DIRECTORY = "page.html";

    private static final String REGEX_SRC = "href=\"/";


    public static void main(String[] args) {
        try {
            download(TRETTON_37_BASE_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void download(String urlString) throws IOException {
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

    }

    private static List<String> findChildDirs(String htmlContent) {
        Document document = Jsoup.parse(htmlContent);

        Elements elements = document.select("a[href]");

        Document clone = document.clone();

        Iterator<Element> iterator = elements.iterator();

        while (iterator.hasNext()) {
            Element element = iterator.next();
            String href = element.attr("href");
            if (href.contains("http")) {
                elements.remove(element);
            }
        }

        for (Element link : elements) {
            String href = link.attr("href");
            System.out.println(href);
        }

        return null;
    }
}