import java.util.HashMap;

public class DownloadedDirs {

    private HashMap<String, Boolean> directories;

    public DownloadedDirs(HashMap<String, Boolean> directories) {
        this.directories = directories;
    }

    public synchronized void markAsDownloaded(String key) {
        directories.put(key, true);
    }

    public synchronized HashMap<String, Boolean> getDirs() {
        return directories;
    }

    public void addDir(String key, Boolean value) {
        directories.put(key, value);
    }

    public Boolean allDownloaded() {
        for (Boolean downloaded : directories.values()) {
            if (downloaded.equals(Boolean.FALSE)) {
                return false;
            }
        }
        return true;
    }

    public void printDirs() {
        directories.forEach((key, value) -> System.out.println(key + " " + value));
    }

}
