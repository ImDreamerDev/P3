package dk.aau.ds304e18.math;

import dk.aau.ds304e18.estimatetime.Estimate;
import dk.aau.ds304e18.estimatetime.EstimateTimeCallable;
import dk.aau.ds304e18.math.Maths;
import dk.aau.ds304e18.math.CalculateLambda;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import dk.aau.ds304e18.sequence.Sequence;

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

        //Go through all the sequences made
        while (i < monteCarloRepeats) {

            //If there is no more random sequences break
            if (randomSequences[i] == null)
                break;

            //Add whatever is returned to the time list
            time.add(estimateTime(project, true, i));

            i++;
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
        for (Task task : tempRecList) {

            task.setStartTime(0d);

            /*int count = 0;
            int bigL = -1;
            int secondBiggestL = -1;*/

            if (startTimes.size() < project.getNumberOfEmployees())
                startTimes.add(task.getStartTime() + task.getEstimatedTime());
            else {
                int temp = startTimes.indexOf(Collections.min(startTimes));
                task.setStartTime(startTimes.get(temp));
                startTimes.set(temp, task.getStartTime() + task.getEstimatedTime());
            }

            for (Task dependency : task.getDependencies())
                if (task.getStartTime() < dependency.getStartTime() + dependency.getEstimatedTime())
                    task.setStartTime(dependency.getStartTime() + dependency.getEstimatedTime());

            /*for (int l = 0; l < k; l++) {
                if (tempRecList.get(k).getStartTime() < tempRecList.get(l).getStartTime() + tempRecList.get(l).getEstimatedTime()) {
                    count++;
                    if(bigL == -1 || tempRecList.get(l).getStartTime() + tempRecList.get(l).getEstimatedTime() > tempRecList.get(bigL).getStartTime() + tempRecList.get(bigL).getEstimatedTime()){
                        secondBiggestL = bigL;
                        bigL = l;
                    } else if (secondBiggestL == -1 || tempRecList.get(l).getStartTime() + tempRecList.get(l).getEstimatedTime() > tempRecList.get(secondBiggestL).getStartTime() + tempRecList.get(secondBiggestL).getEstimatedTime())
                        secondBiggestL = l;
                }
                if (count >= project.getNumberOfEmployees()) {
                    if(secondBiggestL == -1)
                        secondBiggestL = bigL;
                    tempRecList.get(k).setStartTime(tempRecList.get(secondBiggestL).getStartTime() + tempRecList.get(secondBiggestL).getEstimatedTime());
                    l = -1;
                    bigL = -1;
                    //secondBiggestL = -1;
                    count = 0;
                }
            }*/

            //System.out.println(task.getName() + ": " + task.getStartTime());
        }

        //Set the start times so it actually looks fine in output


        //SOUT
        System.out.println("Worst Path: " + worstSequence);
        System.out.println("Worst Time: " + worstTime);
        System.out.println("Best Path: " + bestSequence);
        System.out.println("Best Time: " + bestTime);

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

    /**
     * The main estimateTime function used to estimate the time of a project
     *
     * @param project           The project you want estimated
     * @param monteCarloRepeats The amount of times you want it repeated
     * @param index             The index of the possible loop this is called in (If no loop, send 0)
     * @param random            Is it a random sequence or not.
     * @return Returns the estimated time of the project
     */
    public static double estimateTime(Project project, int monteCarloRepeats, boolean random, int index) {
        //Adds a list to the index of the possibleCompletions list on the project
        project.getTempPossibleCompletions().add(index, new ArrayList<>());
        for (Task task : project.getTasks())
            task.getStartTimeList().add(index, 0d);
        //Gets the task list from the project
        List<Task> taskList = ParseSequence.parseToSingleList(project, false, random, index);
        //For each task in taskList
        for (Task task : taskList) {
            //If the task does not have a lambda yet
            if (task.getLambda() == -1) {
                //Calculate the lambda and optimize the mu value
                List<Double> temp = CalculateLambda.calculateLambda(task.getEstimatedTime(), task.getProbabilities());
                task.getInvG().setParams(temp.get(0), temp.get(1));
            }
        }

        //Find number of threads
        int numOfThreads = Runtime.getRuntime().availableProcessors();

        double duration = 0.0;

        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
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
                for (Task task : project.getTasks())
                    task.getStartTimeList().set(index, task.getStartTimeList().get(index) + fut.get().getStartTimes().get(task) / monteCarloRepeats);

                List<Double> workingList = project.getTempPossibleCompletions().get(index);
                while (workingList.size() < tempList.size())
                    workingList.add(0d);


                //Add all the values to the index of the possibleCompletions
                for (int i = 0; i < tempList.size(); i++) {
                    workingList.set(i,
                            workingList.get(i) + tempList.get(i));
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