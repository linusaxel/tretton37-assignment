import java.util.Map;
import java.util.concurrent.*;

public class Main {

    private static final int NUM_THREADS = 10;

    public static void main(String[] args) {

        System.out.println();

        //Store URL to a concurrent hash map used as a common resource that tracks which paths have been downloaded
        String baseURL = args[0];
        ConcurrentHashMap<String, Boolean> pathsMap = new ConcurrentHashMap<>();
        pathsMap.put(baseURL, false);

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        //Limit to 5 for now?
        for (int i = 0; i < 3; i++) {
            for (Map.Entry<String, Boolean> entry : pathsMap.entrySet()) {
                if (entry.getValue().equals(Boolean.FALSE)) {
                    Future<ConcurrentHashMap<String, Boolean>> future = executorService.submit(new DownloadCallable(pathsMap, entry.getKey()));
                    try {
                        pathsMap = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

    }
}
