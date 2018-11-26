package dk.aau.ds304e18.math;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MonteCarloExecutorService {
    public static ExecutorService executor;

    public static ExecutorService getExecutor() {
        if (executor == null) {
            MonteCarloExecutorService.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        return executor;
    }

    public static void init() {
        if (executor == null || executor.isTerminated()) {
            MonteCarloExecutorService.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
    }

    public static void shutdownExecutor() {
        if (executor == null) return;

        if (!executor.isTerminated()) executor.shutdown();

        executor = null;
    }

    public static void shutdownNow() {

        executor.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                    System.err.println("executor did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
