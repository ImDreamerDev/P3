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

        //Calls the function with the default value 10000
        findFastestSequence(project, 10000);

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
        int counter = 0;

        //TODO: Really needs some optimizing - Actually maybe not, it's fast apparently
        while (j < monteCarloRepeats) {

            boolean cont = false;

            if (counter >= 1000000) {
                System.out.println(j+1);
                break;
            }

            if (j == Calc.amountMax(project.getTasks().size()))
                break;

            randomSequences[j] = Sequence.findRandomSequence(project);
            for (int k = 0; k < j; k++) {
                if (randomSequences[k].equals(randomSequences[j])) {
                    cont = true;
                    break;
                }
            }

            if (cont) {
                counter++;
                continue;
            }

            counter = 0;
            j++;

        }

        while (i < monteCarloRepeats) {

            if (randomSequences[i] == null)
                break;

            String tempSeq = randomSequences[i];
            time.add(estimateTime(tempSeq, project.getNumberOfEmployees(), project.getTasks(), project));

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

    public static double estimateTime(String path, double numOfEmps, List<Task> tasks, Project realPro) {
        Project project = new Project(-1, "Temp", ProjectState.ONGOING, path, 0d, path, numOfEmps);

        for(int i = 0; i < realPro.getPossibleCompletions().size(); i++)
            project.getPossibleCompletions().add(i, realPro.getPossibleCompletions().get(i));

        for (Task task : tasks)
            project.addNewTask(task);
        double temp = estimateTime(project);
        for (Task task : tasks)
            realPro.addNewTask(task);

        for(int i = 0; i < project.getPossibleCompletions().size(); i++)
            try {
                realPro.getPossibleCompletions().set(i, project.getPossibleCompletions().get(i));
            } catch(IndexOutOfBoundsException e) {
                realPro.getPossibleCompletions().add(i, project.getPossibleCompletions().get(i));
            }

        return temp;
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
        List<Future<List<List<Double>>>> list = new ArrayList<>();
        //Create MyCallable instance
        for (int i = 0; i < numOfThreads; i++) {
            Callable<List<List<Double>>> callable = new EstimateTimeCallable(taskList, project.getNumberOfEmployees(), numOfThreads, monteCarloRepeats);
            //submit Callable tasks to be executed by thread pool
            Future<List<List<Double>>> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }

        List<Double> tempList;
        for (Future<List<List<Double>>> fut : list) {
            try {
                // because Future.get() waits for task to get completed
                duration = duration + fut.get().get(0).get(0);
                tempList = fut.get().get(1);

                for(int i = 0; i < tempList.size(); i++){
                    while (project.getPossibleCompletions().size() < tempList.size())
                        project.getPossibleCompletions().add(0d);

                    project.getPossibleCompletions().set(i, project.getPossibleCompletions().get(i) + tempList.get(i));

                }
                //System.out.println(temppp);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        //shut down the executor service now
        executor.shutdown();
        return duration / monteCarloRepeats;
    }
}