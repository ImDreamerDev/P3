package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.math.Calc;
import dk.aau.ds304e18.math.CalculateLambda;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class MonteCarlo {

    public static void findFastestSequence(Project project) {

        //Calls the function with the default value 200 - Might up this default value when/if we optimize estimateTime
        //This might be enough if we find a better way of finding random sequences
        findFastestSequence(project, 200, true);

    }

    public static void findFastestSequence(Project project, boolean fast) {

        findFastestSequence(project, 200, fast);

    }

    public static void findFastestSequence(Project project, int monteCarloRepeats, boolean fast) {

        //Resets the possible completions every time so it's accurate
        project.getPossibleCompletions().clear();

        //2 index integers
        int i = 0;
        int j = 0;

        //The strings that will hold the best and worst sequence
        String bestSequence;
        String worstSequence;

        //The times that will hold the best and worst time
        double bestTime;
        double worstTime; //May be used in the future

        //Initialize an array of strings, we use array because array is faster than list
        String[] randomSequences = new String[monteCarloRepeats];

        //Initialize a list of doubles which we will use as temporary placeholder
        List<Double> time = new ArrayList<>();

        //Used to check if we can find any other random sequences within a million tries
        int counter = 0;

        //TODO: Really needs some optimizing - Actually maybe not, it's fast apparently
        while (j < monteCarloRepeats) {

            //If this is true skip
            boolean cont = false;

            //If the counter hits a million, skip
            if (counter >= 1000000) {
                System.out.println(j + 1);
                break;
            }

            //If there is literally no other possible sequences, skip
            if (j == Calc.amountMax(project.getTasks().size()))
                break;

            //Find a random sequence and work on it
            randomSequences[j] = Sequence.findRandomSequence(project, fast);
            for (int k = 0; k < j; k++) {
                //If it's equal to another one
                if (randomSequences[k].equals(randomSequences[j])) {
                    cont = true;
                    break;
                }
            }

            //Count up the counter and skip
            if (cont) {
                counter++;
                continue;
            }

            //Reset counter
            counter = 0;
            j++;

        }

        //Go through all the sequences made
        while (i < monteCarloRepeats) {

            //If there is no more random sequences break
            if (randomSequences[i] == null)
                break;

            //Use the random sequence at index i and work on it
            String tempSeq = randomSequences[i];
            //Add whatever is returned to the time list
            time.add(estimateTime(tempSeq, project.getNumberOfEmployees(), project.getTasks(), project, i));

            i++;
        }

        //Set temporary index to the index of the minimum
        int tempI = time.indexOf(Collections.min(time));

        //If it's not 0 (i.e. the first one)
        if (tempI != 0) {
            //Clear the first one so we can fill it with the correct numbers
            project.getPossibleCompletions().get(0).clear();

            //TODO: Why loop here? (Dodo)
            //Go through all the numbers in the minimum list
            for (int k = 0; k < project.getPossibleCompletions().get(tempI).size(); k++) {

                //Add all of the minimum list to the first list
                project.getPossibleCompletions().get(0).addAll(project.getPossibleCompletions().get(tempI));

            }
        }
        //Set the variables to correct stuff
        bestTime = Collections.min(time);
        bestSequence = randomSequences[time.indexOf(Collections.min(time))];
        worstTime = Collections.max(time);
        worstSequence = randomSequences[time.indexOf(Collections.max(time))];

        //Set the projects values to correct stuff
        project.setRecommendedPath(bestSequence);
        project.setDuration(bestTime);

        //SOUT
        System.out.println("Worst Path: " + worstSequence);
        System.out.println("Worst Time: " + worstTime);
        System.out.println("Best Path: " + bestSequence);
        System.out.println("Best Time: " + bestTime);

    }

    /**
     * If you want to estimate time without having a project
     *
     * @param path      The path which will be estimated
     * @param numOfEmps The amount of employees on the project
     * @param tasks     The tasks that will be estimated
     * @param project   The actual project the tasks are on
     * @param index     Index of the loop this is called inside, if there is no loop, send 0
     * @return The estimated time
     */
    public static double estimateTime(String path, double numOfEmps, List<Task> tasks, Project project, int index) {
        Project tempProject = new Project(-1, "Temp", ProjectState.ONGOING, path, 0d, path, numOfEmps);

        tempProject.setPossibleCompletions(new ArrayList<>(project.getPossibleCompletions()));
        //   for (int i = 0; i < project.getPossibleCompletions().size(); i++)
        //     tempProject.getPossibleCompletions().add(i, project.getPossibleCompletions().get(i));

        for (Task task : tasks)
            tempProject.addNewTask(task);
        double temp = estimateTime(tempProject, index);
        //TODO: Why added them back to the project?
        for (Task task : tasks)
            project.addNewTask(task);

        project.setPossibleCompletions(new ArrayList<>(tempProject.getPossibleCompletions()));
        //  for (int i = 0; i < tempProject.getPossibleCompletions().size(); i++)
        // try {
        // project.getPossibleCompletions().add(i, tempProject.getPossibleCompletions().get(i));
        //} catch (IndexOutOfBoundsException e) {
        //   project.getPossibleCompletions().add(i, tempProject.getPossibleCompletions().get(i));
        //}

        return temp;
    }

    /**
     * If you have a project you want to estimate the time in
     *
     * @param project The project you want estimated
     * @return The estimated time
     */
    public static double estimateTime(Project project) {
        return estimateTime(project, 0);
    }

    /**
     * If you have a project in a loop you want estimated
     *
     * @param project The project you want estimated
     * @param index   The index of the loop
     * @return The estimated time
     */
    public static double estimateTime(Project project, int index) {
        //Calls the function with the default value 10000
        return estimateTime(project, false, 10000, index);
    }

    /**
     * The main estimateTime function used to estimate the time of a project
     *
     * @param project           The project you want estimated
     * @param rec               If it should estimate the recommended path or the normal path
     * @param monteCarloRepeats The amount of times you want it repeated
     * @param index             The index of the possible loop this is called in (If no loop, send 0)
     * @return Returns the estimated time of the project
     */
    public static double estimateTime(Project project, boolean rec, int monteCarloRepeats, int index) {
        //Adds a list to the index of the possibleCompletions list on the project
        project.getPossibleCompletions().add(index, new ArrayList<>());
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

        //Find number of threads
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

        //Temporary list that will have the values we later add to the possibleCompletions list
        List<Double> tempList;
        for (Future<List<List<Double>>> fut : list) {
            try {
                // because Future.get() waits for task to get completed
                duration = duration + fut.get().get(0).get(0);
                tempList = fut.get().get(1);

                //Add all the values to the index of the possibleCompletions
                for (int i = 0; i < tempList.size(); i++) {
                    while (project.getPossibleCompletions().get(index).size() < tempList.size())
                        project.getPossibleCompletions().get(index).add(0d);
                    project.getPossibleCompletions().get(index).set(i, project.getPossibleCompletions().get(index).get(i) + tempList.get(i));

                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        //shut down the executor service now
        executor.shutdown();
        return duration / monteCarloRepeats;
    }
}