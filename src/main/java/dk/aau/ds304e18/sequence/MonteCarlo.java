package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.CalculateLambda;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.ui.InputTab;
import javafx.concurrent.Worker;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonteCarlo {

    public static void findFastestSequence(Project project) {

        //Calls the function with the default value 100
        //If it finds a significant difference within the first 100, it will try another 100 etc.
        findFastestSequence(project, 100);

    }

    public static void findFastestSequence(Project project, int monteCarloRepeats) {

        int i = 0;
        String bestSequence = "";
        String worstSequence = "";
        double bestTime = -1;
        double worstTime = -1; //May be used in the future

        while (i < monteCarloRepeats) {

            project.setRecommendedPath(findRandomSequence(project));
            estimateTime(project, true);

            if (project.getDuration() > worstTime || worstTime == -1) {
                worstSequence = project.getRecommendedPath();
                worstTime = project.getDuration();
            }

            if (project.getDuration() < bestTime || bestTime == -1) {
                //Set the best sequences and best times
                bestSequence = project.getRecommendedPath();
                bestTime = project.getDuration();
            }

            i++;
        }

        project.setRecommendedPath(bestSequence);
        project.setDuration(bestTime);

        System.out.println("Worst Path: " + worstSequence);
        System.out.println("With Time: " + worstTime);

    }

    private static String findRandomSequence(Project project) {

        int tasksLeft = project.getTasks().size();
        List<Task> tasksSequenced = new ArrayList<>();
        List<Task> tasksNotSequenced = new ArrayList<>(project.getTasks());
        List<Task> tasksToBeRemoved = new ArrayList<>();
        Collections.shuffle(tasksNotSequenced);

        //TODO: Optimize this to actually give relevant paths for multiple employees (etc. if there are 2 employees, the first 2 tasks shouldn't have dependencies if possible
        while (tasksLeft > 0) {
            for (Task task : tasksNotSequenced) {
                if (!tasksSequenced.containsAll(task.getDependencies())) continue;
                tasksSequenced.add(task);
                tasksToBeRemoved.add(task);
                tasksLeft--;
            }

            for (Task task : tasksToBeRemoved)
                tasksNotSequenced.remove(task);
            tasksToBeRemoved = new ArrayList<>();
        }

        return ParseSequence.unparseList(new StringBuilder(), tasksSequenced, tasksNotSequenced.size()).toString();

    }

    public static void estimateTime(Project project) {

        //Calls the function with the default value 10000
        estimateTime(project, false, 10000);

    }

    public static void estimateTime(Project project, boolean rec) {

        //Calls the function with the default value 10000
        estimateTime(project, rec, 10000);

    }

    /**
     * Estimates the time assuming only one task can be done at a time from a project and an amount of time to repeat the tasks
     *
     * @param project           the project where we want to find the estimated duration
     * @param monteCarloRepeats how many times we want to repeat the project schedule (Higher number will be more accurate but will take longer time)
     */
    public static void estimateTime(Project project, boolean rec, int monteCarloRepeats) {
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

        List<javafx.concurrent.Task<Double>> tasks = new ArrayList<>();
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
                    project.setDuration(results.stream().mapToDouble(value -> value).sum() / monteCarloRepeats);
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

    }
}