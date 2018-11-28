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

    //Find number of threads
    private static final int numOfThreads = MonteCarloExecutorService.getNumOfThreads();

    private static final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();

    /**
     * TODO: SOMEONE COMMENT THIS I DIDN'T MAKE THIS REEEE I ASSUME IT JUST RETURNS THE PROGRESS SO THE PROGRESSBAR CAN BE UPDATED REEEEEE
     *
     * @return progress
     */
    public static ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    /**
     * Calculates the project information:
     * The best sequence,
     * The time of this sequence,
     * The probability of completing at different times,
     * Sets the start time of each task for the gantt view,
     * The recommended amount of employees.
     *
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

    /**
     * Finds an array of random sequences in a project.
     * If there's only 1 work group it just finds a random sequence
     * As all sequences would take the same amount of time assuming they're legal
     *
     * @param amountSequences Amount of sequences wanted (max)
     * @param numOfWorkGroups Amount of work groups in the project
     *                        (Slightly faster to just send the integer than getting it from project)
     * @param project The project we want to find random sequences for
     * @param fast Finds less sequences, which is faster, but less accurate
     *             (Only faster with relatively small projects)
     *
     * @return An array of strings with random sequences for the project, all legal
     */
    private static String[] findRandomSequences(int amountSequences, int numOfWorkGroups, Project project, boolean fast) {

        project.setPossibleSequences(new String[amountSequences]);
        String[] randomSequences = project.getPossibleSequences();
        int j = 0;

        //Used to check if we can find any other random sequences within a million tries
        int counter = 0;

        if (numOfWorkGroups > 1) {
            while (j < amountSequences) {

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

                if (j == amountSequences)
                    System.out.println(j);
            }
        } else {
            randomSequences[0] = Sequence.findRandomSequence(project, fast);
        }

        return randomSequences;
    }

    /**
     * Calculates the lambda value (And an optimized mu value) for each task in a given project
     * Sets the tasks inverse gaussian parameters (mu and lambda)
     *
     * @param project The project with the tasks that will be calculated
     */
    private static void calculateLambdaForAllTasks(Project project) {
        for (Task task : project.getTasks()) {
            //If the task does not have a lambda yet
            //Calculate the lambda and optimize the mu value
            List<Double> temp = CalculateLambda.calculateLambda(task.getEstimatedTime(), task.getProbabilities());
            task.getInvG().setParams(temp.get(0), temp.get(1));
        }
    }

    /**
     * Estimates the time for all sequences and returns a list of doubles.
     * This makes it possible to find the best sequence as that is
     * the one with the same index as the smallest time.
     *
     * @param amountSequences The amount of sequences to be tried
     * @param randomSequences The sequences to be tried
     * @param project The project with the sequences
     *
     * @return The list of estimated times
     */
    private static List<Double> estimateTimeForAllSequences(int amountSequences, String[] randomSequences, Project project) {
        int i = 0;
        List<Double> time = new ArrayList<>();
        while (i < amountSequences) {

            //If there is no more random sequences break
            if (randomSequences[i] == null)
                break;

            //Add whatever is returned to the time list
            time.add(estimateTime(project, 10000, true, i, false));

            i++;
            progress.set((double) i / amountSequences);
        }

        return time;
    }

    /**
     * Sets all the doubles in a list that has the same value as the smallest value to the first value that is
     * higher than the smallest value - Also sorts the list from smallest to highest
     * i.e.
     * [1, 1, 1, 9, 5, 3]
     * would become
     * [3, 3, 3, 3, 5, 9]
     *
     * @param list The list in which to set the smallest values to the first value above
     */
    public static void allLowestToNextLowest(List<Double> list) {
        int index = 0;

        Collections.sort(list);

        for (Double tempStartTime : list) {
            if (tempStartTime > list.get(0)) {
                index = list.indexOf(tempStartTime);
                break;
            }
        }

        double minUpper = list.get(index);

        for (int k = 0; k < index; k++)
            list.set(k, minUpper);
    }

    /**
     * Sets the start times of tasks to an available time slot depending on amount of work groups and dependencies
     * This is used to create a reasonable gantt view
     *
     * @param project The project in which the tasks exist
     * @param numOfWorkGroups The amount of work groups
     */
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

                if (NotLegal(task, alreadyStarted))
                    continue;

                if (startTimes.size() < numOfWorkGroups && startTimes.size() < withoutDeps.size()) {
                    if (NotLegal(task, alreadyStarted))
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
                    if (NotLegal(task, alreadyStarted))
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

    /**
     * Finds the smallest amount of work groups needed to get
     * within a margin of the estimated time of the calculated project.
     * Finds the smallest amount of work groups to get the project under a certain time.
     *
     * I.e. The project takes 100 duration
     * Finds the smallest amount of work groups needed to be below 110 duration
     * Finds the smallest amount of work groups needed to be below 90 duration
     *
     * @param project The project to optimize number of work groups
     * @param numOfWorkGroups The number of work groups in the project
     *
     * @return Returns the recommended amount of employees and their estimated time
     */
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

    /**
     * Finds lower bound of a number of work groups.
     * Finds upper bound of a number of work groups.
     * So the program doesn't calculate forever.
     *
     * @param numOfWorkGroups Amount of work groups currently in the project
     *
     * @return An array of integers, first one being the lower bound, second being the upper bound.
     */
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

    /**
     * Estimates the time for a project to complete with the sequence found for the current amount of employees.
     * This gives a guess at an optimal amount of employees.
     *
     * @param amountEmployees The amount of employees to guess with
     * @param numOfWorkGroups The amount of employees the project has at the start
     * @param project The project to calculate
     * @param tempRecEmp The result
     *
     * @return 0 = Skip this one, 1 = Estimation is done, 2 = No need to guess under the current amount of employees
     */
    private static int estimateWithDifferentAmountOfWorkGroups(int amountEmployees, int numOfWorkGroups, Project project, RecommendedEmployees tempRecEmp) {
        if (amountEmployees == numOfWorkGroups) return 0;
        //Set the amount of employees to the set number of employees just to check them
        project.setNumberOfEmployees(amountEmployees);
        //Calculate the estimated time with the current sequence
        //We just want to give a guess, not give an extremely accurate estimate at different employee group numbers
        double tempEst = estimateTime(project, 10000, false, 0, true);
        //If it's within a margin add it to the list
        if (tempEst < project.getDuration() * 0.9 && amountEmployees > numOfWorkGroups ||
                tempEst < project.getDuration() * 1.1 && amountEmployees < numOfWorkGroups) {
            //Add it to the recommended amount list
            tempRecEmp.add(amountEmployees, tempEst);
            System.out.println(amountEmployees + " amount of employees has time " + tempEst);
            if (amountEmployees > numOfWorkGroups)
                return 1;
            return 2;
        }
        return 1;
    }

    /**
     * Checks if the task currently being put into a schedule can legally be put in there.
     *
     * @param task The task to check if legal to put in.
     * @param alreadyStarted The list of tasks already in.
     *
     * @return A boolean - True = Not legal - False = Legal
     */
    private static boolean NotLegal(Task task, List<Task> alreadyStarted) {
        return !alreadyStarted.containsAll(task.getDependencies());
    }

    /**
     * Checks if the task is ready to be put in after the task that is done the fastest currently.
     *
     * @param task The task to check
     * @param startTimes The current startTimes available
     *
     * @return The index of the startTime to be placed in, -1 if illegal
     */
    private static int findSmallestPossible(Task task, List<Double> startTimes) {
        if (task.getDependencies().size() == 0)
            return startTimes.indexOf(Collections.min(startTimes));

        for (Task dependency : task.getDependencies()) {
            if (dependency.getStartTime() + dependency.getEstimatedTime() > Collections.min(startTimes))
                return -1;

        }

        return startTimes.indexOf(Collections.min(startTimes));
    }

    /**
     * The main estimateTime function used to estimate the time of a project
     *
     * @param project           The project you want estimated.
     * @param monteCarloRepeats The amount of times you want it repeated.
     * @param random            Is it a random sequence or not.
     * @param index             The index of the possible loop this is called in (If no loop, send 0).
     * @param rec               If it should use the recommended path or not.
     *
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