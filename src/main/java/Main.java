import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {

        //Store URL from program argument
        String baseURL = args[0];

        //Store URL to a common resource that tracks which URLs have been downloaded
        HashMap<String, Boolean> dirs = new HashMap<>();
        dirs.put(baseURL, false);
        DownloadedDirs downloadedDirs = new DownloadedDirs(dirs);

        //Create first thread that downloads from baseURL
        HTMLDownloader thread1 = new HTMLDownloader(downloadedDirs, baseURL);
        thread1.start();
        thread1.join();

        downloadedDirs.printDirs();

        for (Map.Entry<String, Boolean> entry : downloadedDirs.getDirs().entrySet()) {
            if (entry.getValue().equals(Boolean.FALSE)) {
                HTMLDownloader thread = new HTMLDownloader(downloadedDirs, entry.getKey());
                thread.start();
            }
        }

        downloadedDirs.printDirs();
    }
}
