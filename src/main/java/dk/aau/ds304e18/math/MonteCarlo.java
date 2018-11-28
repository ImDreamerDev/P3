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

    /**
     * TODO: SOMEONE COMMENT THIS I DIDN'T MAKE THIS REEEE I ASSUME IT JUST RETURNS THE PROGRESS SO THE PROGRESSBAR CAN BE UPDATED REEEEEE
     * @return progress
     */
    public static ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    /**
     * Calculates the project information
     * The best sequence,
     * The time of this sequence,
     * The probability of completing at different times
     * Sets the start time of each task for the gantt view
     * The recommended amount of employees
     * @param project The project you want to calculate and set values for
     * @param amountSequences The amount of sequences you want
     * @param fast Less accurate, but possibly faster
     */
    public static void calculateProjectInformation(Project project, int amountSequences, boolean fast) {
        //Resets the possible completions every time so it's accurate
        project.getPossibleCompletions().clear();
        int numOfWorkGroups = (int) project.getNumberOfEmployees();

        //The strings that will hold the best and worst sequence
        String bestSequence;
        String worstSequence;

        //The times that will hold the best and worst time
        double bestTime;
        double worstTime; //May be used in the future

        //Initialize an array of strings, we use array because array is faster than list
        String[] randomSequences = findRandomSequences(amountSequences, numOfWorkGroups, project, fast);

        //Calculate the lambda value for all tasks and look for optimizing the mu value
        calculateLambdaForAllTasks(project);

        //Go through all the sequences made and add them to the temporary list "time"
        List<Double> time = estimateTimeForAllSequences(amountSequences, randomSequences, project);

        //Set the variables to correct stuff
        bestTime = Collections.min(time);
        bestSequence = randomSequences[time.indexOf(bestTime)];

        //This is only used for fun - To see how big the difference is between the worst and best sequence
        worstTime = Collections.max(time);
        worstSequence = randomSequences[time.indexOf(worstTime)];

        //Set the projects values to correct stuff
        project.setRecommendedPath(bestSequence);
        project.setDuration(bestTime);
        project.getPossibleCompletions().addAll(project.getTempPossibleCompletions().get(time.indexOf(bestTime)));
        setStartTimesOfTasks(project, numOfWorkGroups);
        project.setRecommendedEmployees(optimizeWorkGroups(project, numOfWorkGroups));

        //Set amount of employees back to what it was
        project.setNumberOfEmployees(numOfWorkGroups);

        //SOUT
        System.out.println("Worst Path: " + worstSequence);
        System.out.println("Worst Time: " + worstTime);
        System.out.println("Best Path: " + bestSequence);
        System.out.println("Best Time: " + bestTime);

    }

    private static String[] findRandomSequences(int monteCarloRepeats, int numOfWorkGroups, Project project, boolean fast) {

        project.setPossibleSequences(new String[monteCarloRepeats]);
        String[] randomSequences = project.getPossibleSequences();
        int j = 0;

        //Used to check if we can find any other random sequences within a million tries
        int counter = 0;

        if (numOfWorkGroups > 1) {
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

        return randomSequences;
    }

    private static void calculateLambdaForAllTasks(Project project) {
        for (Task task : project.getTasks()) {
            //If the task does not have a lambda yet
            //Calculate the lambda and optimize the mu value
            List<Double> temp = CalculateLambda.calculateLambda(task.getEstimatedTime(), task.getProbabilities());
            task.getInvG().setParams(temp.get(0), temp.get(1));
        }
    }

    private static List<Double> estimateTimeForAllSequences(int monteCarloRepeats, String[] randomSequences, Project project) {
        int i = 0;
        List<Double> time = new ArrayList<>();
        while (i < monteCarloRepeats) {

            //If there is no more random sequences break
            if (randomSequences[i] == null)
                break;

            //Add whatever is returned to the time list
            time.add(estimateTime(project, true, i));

            i++;
            progress.set((double) i / monteCarloRepeats);
        }

        return time;
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

    private static void setStartTimesOfTasks(Project project, int numOfWorkGroups) {
        List<Double> startTimes = new ArrayList<>();
        List<Task> tempRecList = ParseSequence.parseToSingleList(project, true);
        List<Task> alreadyStarted = new ArrayList<>();
        List<Task> withoutDeps = new ArrayList<>();
        boolean stuffChanged;
        for (Task task : tempRecList) {
            if (task.getDependencies().size() == 0)
                withoutDeps.add(task);
            task.setStartTime(-1);
        }

        for (int count = 0; count < tempRecList.size(); ) {
            stuffChanged = false;
            for (Task task : tempRecList) {

                if (task.getStartTime() != -1)
                    continue;

                if (check(task, alreadyStarted))
                    continue;

                if (startTimes.size() < numOfWorkGroups && startTimes.size() < withoutDeps.size()) {
                    if (check(task, alreadyStarted))
                        continue;
                    if (task.getStartTime() == -1)
                        task.setStartTime(0d);
                    startTimes.add(task.getStartTime() + task.getEstimatedTime());
                    alreadyStarted.add(task);
                    stuffChanged = true;
                } else if (startTimes.size() < numOfWorkGroups && startTimes.size() >= withoutDeps.size()) {
                    while (startTimes.size() < numOfWorkGroups)
                        startTimes.add(0d);
                    continue;
                } else {
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
    }

    private static RecommendedEmployees optimizeWorkGroups(Project project, int numOfWorkGroups) {
        //Initialize the temp amount of employee groups and the temp recommended employees
        RecommendedEmployees tempRecEmp = new RecommendedEmployees();

        //Initialize lower and upperBound and make sure no funny business happens
        int[] lowUp = findLowUp(numOfWorkGroups);

        //Calculate the estimated time with the different amount of employee groups
        for (int i = lowUp[0]; i <= lowUp[1]; i++) {
            int temp = estimateWithDifferentAmountOfWorkGroups(i, numOfWorkGroups, project, tempRecEmp);
            if(temp == 0) continue;
            if(temp == 1) break;
            if(temp == 2) i = numOfWorkGroups;
        }

        return tempRecEmp;

    }

    private static int[] findLowUp(int numOfWorkGroups) {
        int lowerBound = (int) (numOfWorkGroups * 0.5);
        if (lowerBound == numOfWorkGroups)
            lowerBound -= 1;
        if (lowerBound == 0)
            lowerBound = 1;

        int upperBound = (int) (numOfWorkGroups * 1.5);
        if (upperBound == numOfWorkGroups)
            upperBound += 1;

        return new int[]{lowerBound, upperBound};
    }

    private static int estimateWithDifferentAmountOfWorkGroups(int i, int numOfWorkGroups, Project project, RecommendedEmployees tempRecEmp) {
        if (i == numOfWorkGroups) return 0;
        //Set the amount of employees to the set number of employees just to check them
        project.setNumberOfEmployees(i);
        //Calculate the estimated time with the current sequence
        //We just want to give a guess, not give an extremely accurate estimate at different employee group numbers
        double tempEst = estimateTime(project, true);
        //If it's within a margin add it to the list
        if (tempEst < project.getDuration() * 0.95 && i > numOfWorkGroups || tempEst < project.getDuration() * 1.05 && i < numOfWorkGroups) {
            //Add it to the recommended amount list
            tempRecEmp.add(i, tempEst);
            System.out.println(i + " amount of employees has time " + tempEst);
            if (i > numOfWorkGroups)
                return 1;
            return 2;
        }
        return -1;
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
    private static final int numOfThreads = MonteCarloExecutorService.getNumOfThreads();

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