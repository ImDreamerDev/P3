package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Calc;
import dk.aau.ds304e18.math.CalculateLambda;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class MonteCarlo {

    public static void findFastestSequence(Project project) {

        //Calls the function with the default value 100
        //If it finds a significant difference within the first 100, it will try another 100 etc.
        findFastestSequence(project, 100);

    }

    public static void findFastestSequence(Project project, int monteCarloRepeats) {

        int i = 0;
        int j = 0;
        String bestSequence;
        String worstSequence;
        double bestTime;
        double worstTime; //May be used in the future
        String[] randomSequences = new String[monteCarloRepeats];
        List<Double> time = new ArrayList<>();

        while (j < monteCarloRepeats) {

            boolean cont = false;

            if(j == project.getTasks().size() * (project.getTasks().size() - 1)) //Calc.amountMax(project.getTasks().size()))
                break;

            randomSequences[j] = Sequence.findRandomSequence(project);
            for(int k = 0; k < j; k++) {
                if (randomSequences[k].equals(randomSequences[j])) {
                    cont = true;
                    break;
                }
            }

            if (cont)
                continue;

            j++;

        }

        while (i < monteCarloRepeats) {

            if(randomSequences[i] == null)
                break;

            String tempSeq = randomSequences[i];
            time.add(estimateTime(tempSeq, project.getNumberOfEmployees(), project.getTasks()));

            i++;
        }

        bestTime = Collections.min(time);
        bestSequence = randomSequences[time.indexOf(Collections.min(time))];
        worstTime = Collections.max(time);
        worstSequence = randomSequences[time.indexOf(Collections.max(time))];

        project.setRecommendedPath(bestSequence);
        project.setDuration(bestTime);

        System.out.println("Worst Path: " + worstSequence);
        System.out.println("With Time: " + worstTime);
        System.out.println("Best Path: " + bestSequence);
        System.out.println("Best Time: " + bestTime);

    }

    public static double estimateTime(String path, double numOfEmps, List<Task> tasks) {
        Project project = new Project(-1, "Temp", ProjectState.ONGOING, path, 0d, path, numOfEmps);
        for (Task task : tasks)
            project.addNewTask(task);
        return estimateTime(project);
    }

    public static double estimateTime(Project project) {

        //Calls the function with the default value 10000
        return estimateTime(project, false, 10000);

    }

    public static double estimateTime(Project project, boolean rec) {

        //Calls the function with the default value 10000
        return estimateTime(project, rec, 10000);

    }

    /**
     * Estimates the time assuming only one task can be done at a time from a project and an amount of time to repeat the tasks
     *
     * @param project           the project where we want to find the estimated duration
     * @param monteCarloRepeats how many times we want to repeat the project schedule (Higher number will be more accurate but will take longer time)
     */
    public static double estimateTime(Project project, boolean rec, int monteCarloRepeats) {
        AtomicReference<Double> temp2 = new AtomicReference<>();
        //Gets the task list from the project
        List<Task> taskList = ParseSequence.parseToSingleList(project, rec);
        //For each task in taskList
        for (Task task : taskList) {
            //If the task does not have a lambda yet
            if (task.getLambda() == -1) {
                //Calculate the lambda and optimize the mu value
                List<Double> temp = CalculateLambda.calculateLambda(task.getEstimatedTime(), task.getProbabilities());
                task.setEstimatedTime(temp.get(0));
                task.setLambda(temp.get(1));
            }
        }

        int numOfThreads = Runtime.getRuntime().availableProcessors();

        double duration = 0.0;

        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
        //create a list to hold the Future object associated with Callable
        List<Future<Double>> list = new ArrayList<Future<Double>>();
        //Create MyCallable instance
        Callable<Double> callable = new EstimateTimeCallable(taskList, project.getEmployees().size(), numOfThreads, monteCarloRepeats);
        for (int i = 0; i < numOfThreads; i++) {
            //submit Callable tasks to be executed by thread pool
            Future<Double> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }

        for (Future<Double> fut : list) {
            try {
                // because Future.get() waits for task to get completed
                duration = duration + fut.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        executor.shutdown();

        /*List<javafx.concurrent.Task<Double>> tasks = new ArrayList<>();
        for (int i = 0; i < numOfThreads; i++) {
            tasks.add(new EstimateTimeCallable(taskList, project.getNumberOfEmployees(), numOfThreads, monteCarloRepeats));
        }

        List<Double> results = new ArrayList<>();
        List<ProgressBar> progressBars = new ArrayList<>();
        Instant start = Instant.now();
        for (javafx.concurrent.Task<Double> doubleTask : tasks) {
            if (!DatabaseManager.isTests) {
                ProgressBar bar = new ProgressBar();
                progressBars.add(bar);
                bar.progressProperty().bind(doubleTask.progressProperty());
                InputTab.progressBarContainer.getChildren().add(bar);
                if (tasks.indexOf(doubleTask) == tasks.size() - 1) {
                    Button cancelButton = new Button("Cancel");
                    cancelButton.setOnMouseClicked(event -> tasks.forEach(javafx.concurrent.Task::cancel));
                    cancelButton.setMaxHeight(bar.getHeight());
                    InputTab.progressBarContainer.getChildren().add(cancelButton);
                }
            }
            doubleTask.setOnCancelled(event -> {
                if (!DatabaseManager.isTests) {
                    InputTab.progressBarContainer.getChildren().clear();
                }
            });
            doubleTask.setOnSucceeded(event -> {
                results.add(doubleTask.getValue());
                if (!DatabaseManager.isTests)
                    InputTab.progressBarContainer.getChildren().remove(progressBars.get(tasks.indexOf(doubleTask)));


                if (tasks.stream().allMatch(doubleTask1 -> doubleTask1.getState() == Worker.State.SUCCEEDED)) {
                    temp2.set(results.stream().mapToDouble(value -> value).sum() / monteCarloRepeats);
                    System.out.println("All done");
                    System.out.println(project.getDuration());
                    InputTab.getInstance().updateOutput();
                    Instant end = java.time.Instant.now();
                    Duration between = java.time.Duration.between(start, end);
                    System.out.format((char) 27 + "[31mNote: total in that unit!\n" + (char) 27 + "[39mHours: %02d Minutes: %02d Seconds: %02d Milliseconds: %04d \n",
                            between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis()); // 0D, 00:00:01.1001
                    if (!DatabaseManager.isTests) {
                        InputTab.progressBarContainer.getChildren().clear();
                    }
                }
            });
            new Thread(doubleTask).start();
        }

        //TODO: To Rasmus or who it may concern
        //This returns null because it doesn't wait for it to get assigned in the thing, make it do that please, project is not touched anymore thank you very much
        return temp2.get();*/
        return duration / monteCarloRepeats;
    }
}