package dk.aau.ds304e18.math;

import dk.aau.ds304e18.estimatetime.Estimate;
import dk.aau.ds304e18.estimatetime.EstimateTimeCallable;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import dk.aau.ds304e18.sequence.Sequence;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class MonteCarlo {

    private static final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();

    public double getProgress() {
        return progressProperty().get();
    }

    public static ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

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

        List<Double> startTimes = new ArrayList<>();

        project.setPossibleSequences(new String[monteCarloRepeats]);

        //Initialize an array of strings, we use array because array is faster than list
        String[] randomSequences = project.getPossibleSequences();

        //Initialize a list of doubles which we will use as temporary placeholder
        List<Double> time = new ArrayList<>();

        //Used to check if we can find any other random sequences within a million tries
        int counter = 0;

        //TODO: Really needs some optimizing - Actually maybe not, it's fast apparently
        if (project.getNumberOfEmployees() > 1) {
            while (j < monteCarloRepeats) {

                //If this is true skip
                boolean continueLoop = false;

                //If the counter hits a million, skip
                if (counter >= 1000000) {
                    System.out.println(j + 1);
                    break;
                }

                //If there is literally no other possible sequences, skip
                if (j == Maths.amountMax(project.getTasks().size()))
                    break;

                //Find a random sequence and work on it
                randomSequences[j] = Sequence.findRandomSequence(project, fast);
                for (int k = 0; k < j; k++) {
                    //If it's equal to another one
                    if (randomSequences[k].equals(randomSequences[j])) {
                        continueLoop = true;
                        break;
                    }
                }

                //Count up the counter and skip
                if (continueLoop) {
                    counter++;
                    continue;
                }

                //Reset counter
                counter = 0;
                j++;

                if (j == monteCarloRepeats)
                    System.out.println(j);


            }
        } else {
            randomSequences[0] = Sequence.findRandomSequence(project, fast);
        }

        //For each task in taskList
        for (Task task : project.getTasks()) {
            //If the task does not have a lambda yet
            if (task.getLambda() == -1) {
                //Calculate the lambda and optimize the mu value
                List<Double> temp = CalculateLambda.calculateLambda(task.getEstimatedTime(), task.getProbabilities());
                task.getInvG().setParams(temp.get(0), temp.get(1));
            }
        }

        //Go through all the sequences made
        while (i < monteCarloRepeats) {

            //If there is no more random sequences break
            if (randomSequences[i] == null)
                break;

            //Add whatever is returned to the time list
            time.add(estimateTime(project, true, i));

            i++;
            progress.set((double) i / monteCarloRepeats);
        }

        //Set temporary index to the index of the minimum
        int tempI = time.indexOf(Collections.min(time));

        //System.out.println(project.getTempPossibleCompletions().get(tempI));

        project.getPossibleCompletions().addAll(project.getTempPossibleCompletions().get(tempI));

        //Set the variables to correct stuff
        bestTime = Collections.min(time);
        bestSequence = randomSequences[time.indexOf(Collections.min(time))];
        worstTime = Collections.max(time);
        worstSequence = randomSequences[time.indexOf(Collections.max(time))];

        //Set the projects values to correct stuff
        project.setRecommendedPath(bestSequence);
        project.setDuration(bestTime);
        List<Task> tempRecList = ParseSequence.parseToSingleList(project, true);
        List<Task> alreadyStarted = new ArrayList<>();
        List<Task> withoutDeps = new ArrayList<>();
        boolean stuffChanged;
        for (Task task : tempRecList)
            if (task.getDependencies().size() == 0)
                withoutDeps.add(task);
        for (Task task : tempRecList)
            task.setStartTime(-1);
        for (int count = 0; count < tempRecList.size(); ) {
            stuffChanged = false;
            for (Task task : tempRecList) {

                if (task.getStartTime() != -1)
                    continue;

                if (check(task, alreadyStarted))
                    continue;

                if (startTimes.size() < project.getNumberOfEmployees() && startTimes.size() < withoutDeps.size()) {
                    if (check(task, alreadyStarted))
                        continue;
                    if (task.getStartTime() == -1)
                        task.setStartTime(0d);
                    startTimes.add(task.getStartTime() + task.getEstimatedTime());
                    alreadyStarted.add(task);
                    stuffChanged = true;
                } else if (startTimes.size() < project.getNumberOfEmployees() && startTimes.size() >= withoutDeps.size()) {
                    while(startTimes.size() < project.getNumberOfEmployees())
                        startTimes.add(0d);
                    continue;
                } else{
                    if (check(task, alreadyStarted))
                        continue;
                    int temp = findSmallestPossible(task, startTimes);
                    if (temp == -1)
                        continue;
                    if (task.getStartTime() == -1)
                        task.setStartTime(0d);
                    task.setStartTime(startTimes.get(temp));
                    startTimes.set(temp, task.getStartTime() + task.getEstimatedTime());
                    alreadyStarted.add(task);
                    stuffChanged = true;
                }

                count++;

                //System.out.println(task.getName() + ": " + task.getStartTime());

                break;
            }

            if (!stuffChanged) {

                allLowestToNextLowest(startTimes);

            }
        }

        //Initialize the temp amount of employee groups and the temp recommended employees
        double tempNumOfEmps = project.getNumberOfEmployees();
        RecommendedEmployees tempRecEmp = new RecommendedEmployees();

        //Initialize lower and upperBound and make sure no funny business happens
        int lowerBound = (int)(tempNumOfEmps*0.5);
        if(lowerBound == (int)tempNumOfEmps)
            lowerBound = (int)(tempNumOfEmps-1);
        if(lowerBound < 1)
            lowerBound = 1;

        int upperBound = (int)(tempNumOfEmps*1.5);
        if(upperBound == (int)tempNumOfEmps)
            upperBound = (int)(tempNumOfEmps + 1);

        //Calculate the estimated time with the different amount of employee groups
        for(int k = lowerBound; k <= upperBound; k++) {
            //Set the amount of employees to the set number of employees just to check them
            project.setNumberOfEmployees(k);
            //Calculate the estimated time with the current sequence
            //We just want to give a guess, not give an extremely accurate estimate at different employee group numbers
            double tempEst = estimateTime(project, true);
            //If it's within a margin add it to the list
            if(tempEst < project.getDuration()*0.95 && k > tempNumOfEmps || tempEst < project.getDuration()*1.05 && k < tempNumOfEmps) {
                //Add it to the recommended amount list
                tempRecEmp.add(k, tempEst);
                System.out.println(k + " amount of employees has time " + tempEst);
            }
        }

        //Set the amount of employee groups recommended
        project.setAmountEmpsRecommended(tempRecEmp);

        //Set amount of employeees back to what it was
        project.setNumberOfEmployees(tempNumOfEmps);

        //SOUT
        System.out.println("Worst Path: " + worstSequence);
        System.out.println("Worst Time: " + worstTime);
        System.out.println("Best Path: " + bestSequence);
        System.out.println("Best Time: " + bestTime);

    }

    public static void allLowestToNextLowest(List<Double> startTimes) {
        int index = 0;

        Collections.sort(startTimes);

        for (Double tempStartTime : startTimes) {
            if (tempStartTime > startTimes.get(0)) {
                index = startTimes.indexOf(tempStartTime);
                break;
            }
        }

        double minUpper = startTimes.get(index);

        for (int k = 0; k < index; k++)
            startTimes.set(k, minUpper);
    }

    private static boolean check(Task task, List<Task> alreadyStarted) {
        return !alreadyStarted.containsAll(task.getDependencies());
    }

    private static int findSmallestPossible(Task task, List<Double> startTimes) {
        List<Double> temp = new ArrayList<>(startTimes);

        if (task.getDependencies().size() == 0)
            return temp.indexOf(Collections.min(temp));

        for (Task dependency : task.getDependencies()) {
            //if(bestCase) {
            if (dependency.getStartTime() + dependency.getEstimatedTime() > Collections.min(temp))
                return -1;

        }

        return temp.indexOf(Collections.min(temp));
    }

    /**
     * If you have a project you want to estimate the time in
     *
     * @param project The project you want estimated
     * @return The estimated time
     */
    public static double estimateTime(Project project) {
        return estimateTime(project, 10000, false, 0);
    }

    public static double estimateTime(Project project, boolean rec) {
        return estimateTime(project, 10000, false, 0, rec);
    }

    /**
     * If you have a project you want to estimate the time in with random sequences
     *
     * @param project The project you want estimated
     * @param random  Want to specify that it's random sequences
     * @param index   The index we're at in the random sequences
     * @return The estimated time
     */
    public static double estimateTime(Project project, boolean random, int index) {
        return estimateTime(project, 10000, random, index);
    }

    //Find number of threads
    private static int numOfThreads = Runtime.getRuntime().availableProcessors();

    public static double estimateTime(Project project, int monteCarloRepeats, boolean random, int index) {
        return estimateTime(project, monteCarloRepeats, random, index, false);
    }

    /**
     * The main estimateTime function used to estimate the time of a project
     *
     * @param project           The project you want estimated
     * @param monteCarloRepeats The amount of times you want it repeated
     * @param index             The index of the possible loop this is called in (If no loop, send 0)
     * @param random            Is it a random sequence or not.
     * @return Returns the estimated time of the project
     */
    public static double estimateTime(Project project, int monteCarloRepeats, boolean random, int index, boolean rec) {
        //Adds a list to the index of the possibleCompletions list on the project
        project.getTempPossibleCompletions().add(index, new ArrayList<>());
        //Gets the task list from the project
        List<Task> taskList = ParseSequence.parseToSingleList(project, rec, random, index);

        double duration = 0.0;

        ExecutorService executor = MonteCarloExecutorService.getExecutor();
        //create a list to hold the Future object associated with Callable
        List<Future<Estimate>> list = new ArrayList<>();
        //Create MyCallable instance
        for (int i = 0; i < numOfThreads; i++) {
            Callable<Estimate> callable = new EstimateTimeCallable(taskList, project.getNumberOfEmployees(),
                    monteCarloRepeats / numOfThreads);
            //submit Callable tasks to be executed by thread pool
            Future<Estimate> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }

        //Temporary list that will have the values we later add to the possibleCompletions list
        List<Double> tempList;
        for (Future<Estimate> fut : list) {
            try {
                // because Future.get() waits for task to get completed
                duration = duration + fut.get().getDuration();
                tempList = fut.get().getChances();

                List<Double> workingList = project.getTempPossibleCompletions().get(index);
                while (workingList.size() < tempList.size())
                    workingList.add(0d);

                //Add all the values to the index of the possibleCompletions
                for (int i = 0; i < tempList.size(); i++) {
                    workingList.set(i,
                            workingList.get(i) + tempList.get(i));
                }
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
        }
        return duration / monteCarloRepeats;
    }
}