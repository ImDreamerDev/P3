package dk.aau.ds304e18.math;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MonteCarloExecutorService {
    //The executor service used in monte carlo
    private static ExecutorService executor;

    //The amount of processor threads/cores available
    private static int numOfThreads = Runtime.getRuntime().availableProcessors();

    public static int getNumOfThreads() {
        return numOfThreads;
    }


    /**
     * Gets the executor. If it's null it makes a new one and returns.
     *
     * @return The executor service
     */
    public static ExecutorService getExecutor() {
        if (executor == null) {
            MonteCarloExecutorService.executor = Executors.newFixedThreadPool(numOfThreads);
        }
        return executor;
    }

    /**
     * Makes sure the executor service is not terminated.
     * Should be called once before using the executor.
     */
    public static void init() {
        if (executor == null || executor.isTerminated()) {
            MonteCarloExecutorService.executor = Executors.newFixedThreadPool(numOfThreads);
        }
    }


    /**
     * Gracefully shuts down the executor.
     */
    public static void shutdownExecutor() {
        if (executor == null) return;

        if (!executor.isTerminated()) executor.shutdown();
    }


    /**
     * Forces shutdown of the executor and terminates all running tasks.
     * Used when pressing the stop button while calculating.
     */
    public static void shutdownNow() {
        executor.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                    System.err.println("executor did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread is also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
