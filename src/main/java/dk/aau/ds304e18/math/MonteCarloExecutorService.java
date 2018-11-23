package dk.aau.ds304e18.math;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonteCarloExecutorService {
    public static ExecutorService executor;

    public static ExecutorService getExecutor() {
        if (executor == null || executor.isTerminated()) {
            MonteCarloExecutorService.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        return executor;
    }

    public static void shutdownExecutor() {
        if (executor == null) return;

        if (!executor.isTerminated()) executor.shutdown();

        executor = null;

    }
}
