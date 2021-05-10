import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        String baseURL = args[0];
        int DEPTH = Integer.parseInt(args[1]);

        HashMap<String, Boolean> pathsMap = new HashMap<>();
        pathsMap.put(baseURL, false);

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for(int i = 0; i < DEPTH; i++) {
            downloadPathsAsynchronously(baseURL, pathsMap, executorService);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        System.out.println("Done! Download of " + baseURL + " and child directories successful.");
    }

    private static void downloadPathsAsynchronously(String baseURL, HashMap<String, Boolean> pathsMap, ExecutorService executorService) {
        List<DownloadCallable> taskList = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : pathsMap.entrySet()) {
            taskList.add(new DownloadCallable(entry.getKey()));
            pathsMap.replace(entry.getKey(), true);
        }

        List<Future<List<String>>> resultList = null;
        try {
            resultList = executorService.invokeAll(taskList);
        } catch (InterruptedException e) {
            System.out.println("Couldn't execute tasks");
        }

        for (int i = 0; i < Objects.requireNonNull(resultList).size(); i++) {
            Future<List<String>> futures = resultList.get(i);
            try {
                List<String> paths = futures.get();
                updateMap(baseURL, pathsMap, paths);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Couldn't get futures");
            }
        }
    }

    private static void updateMap(String baseURL, HashMap<String, Boolean> pathsMap, List<String> paths) {
        for (String path : paths) {
            pathsMap.putIfAbsent(baseURL + path, false);
        }
    }
}
