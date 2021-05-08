import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        String baseUrl = args[0];
        String dir = "index";

        HTMLDownloader firstThread = new HTMLDownloader(baseUrl, dir);
        firstThread.start();
        firstThread.join();

        List<String> childDirs = firstThread.getChildDirs();

        for (String childDir : childDirs) {
            HTMLDownloader HTMLDownloader = new HTMLDownloader(baseUrl + childDir, childDir);
            HTMLDownloader.start();
        }

    }

}