import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Downloader {

    public static void main(String[] args) {
        String baseUrl = args[0];
        int depth = Integer.parseInt(args[1]);
        String dir = args[2];

        try {
            download(baseUrl, depth, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void download(String urlString, int depth, String dir) throws IOException {

        if (depth == 0) {
            return;
        }

        File directory = new File(dir);
        if (! directory.exists()){
            directory.mkdir();
        }

        URL url = new URL(urlString);
        List<String> childDirs;
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/" + dir + ".html"))
        ) {
            StringBuilder htmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                htmlContent.append(line);
            }
            System.out.println(htmlContent);
            childDirs = findChildDirs(htmlContent.toString());
        }
        depth--;

        for (String childDir : childDirs) {
            download(urlString + childDir, depth, dir + childDir);
        }
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
        System.out.println("Found dirs:");
        System.out.println(Arrays.toString(childDirs.toArray()));
        childDirs = childDirs.stream().distinct().collect(Collectors.toList());
        return childDirs;
    }
}