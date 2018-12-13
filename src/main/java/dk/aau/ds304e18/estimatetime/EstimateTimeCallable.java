package dk.aau.ds304e18.estimatetime;

import dk.aau.ds304e18.math.InverseGaussian;
import dk.aau.ds304e18.math.MonteCarlo;
import dk.aau.ds304e18.models.Task;

import java.util.*;
import java.util.concurrent.Callable;

public class EstimateTimeCallable implements Callable<Estimate> {
    //The list of tasks in the project
    private final List<Task> taskList;

    //The amount of tasks able to be done in parallel
    private final double amountEmployees;

    //Random used to calculate the time of each task
    private final Random random = new Random();

    //Hashmap for each inverse gaussian for each task
    private final HashMap<Task, InverseGaussian> invG = new HashMap<>();

    //The amount of times to run through, i.e. the amount of times to do the monte carlo divided by amount of threads
    private final int repeats;

    //Constructor for the function
    public EstimateTimeCallable(List<Task> taskList, double amountEmployees, int repeats) {
        this.taskList = new ArrayList<>(taskList);
        this.amountEmployees = amountEmployees;
        this.repeats = repeats;
    }

    public Estimate call() {
        //The chances for each duration put into an array. Each duration is specified by the index
        List<Double> chances = new ArrayList<>();

        //The integer that decides how many parallel tasks can be done
        final int tempBig;

        //Calculates the Inverse Gaussian for each task only once
        for (Task task : taskList)
            invG.put(task, task.getInvG());

        //Figures out how many tasks can be done in parallel
        if (amountEmployees < taskList.size())
            tempBig = (int) amountEmployees;
        else
            tempBig = taskList.size();

        //Initialize the duration of this run through
        double duration = 0.0;

        //Repeat repeats time
        for (int i = 0; i < repeats; i++) {

            //If there's only 1 employee, a lot of stuff is way simpler
            if (amountEmployees > 1) {

                //Initialize the List with an initial capacity so it doesn't have to resize
                List<Task> tasksDone = new ArrayList<>(taskList.size());

                //Initialize the list with an initial capacity so it doesn't have to resize
                List<Double> durations = new ArrayList<>(tempBig);

                //Initialize a hashmap with a task and a double. This hashmap is used to figure out if we can legally put in the task at the place we want to
                HashMap<Task, Double> taskDoneAt = new HashMap<>();

                //We initialize an amaount of durations equal to the amount of tasks done in parallel
                for (int j = 0; j < tempBig; j++) {
                    durations.add(0d);
                }

                //As long as there's still tasks to be checked
                while (taskList.size() != tasksDone.size()) {
                    //As long as j is less than amountEmployees and taskList.size
                    //This means that if there are too many employees for tasks, the run through only happens for
                    //Each employee instead of each task
                    for (int j = 0; j < amountEmployees && j < taskList.size(); j++) {
                        //If we're done checking tasks (As this doesn't always mean we get out of the loop)
                        if (taskList.size() == tasksDone.size()) break;

                        //If the duration isn't the smallest one (i.e. the earliest it can be)
                        //And that the duration isn't on 0, skip this duration and use another one
                        //This will always find the quickest time for each task to be done
                        if (0 + durations.get(j) != 0 + Collections.min(durations) && durations.get(j) != 0) continue;

                        //This bool is used in case there is no task that can be put in this place
                        //Because of dependencies
                        boolean temp = false;

                        //For each task in the taskList (i.e. each task in the project)
                        for (Task task : taskList) {

                            //If it's already put into the tasksDone list, skip it
                            if (tasksDone.contains(task)) continue;

                            //If tasksDone contains all the dependencies (i.e. it's legal to put it in at this point
                            //in the sequence)
                            if (tasksDone.containsAll(task.getDependencies())) {

                                //This boolean is used to check if the task is legal
                                //I.e. if the dependencies are done after this task in the current sequence
                                //Skip the task and try again later
                                boolean skip = false;

                                //For each dependency, check if the dependencies are done at a later point
                                //Than this task is put in, i.e. check if the task can legally be put in here
                                for (Task dependency : task.getDependencies()) {
                                    if (taskDoneAt.get(dependency) > durations.get(j)) {
                                        skip = true;
                                        break;
                                    }
                                }

                                //If it wasn't legal, skip the task and try again later
                                if (skip)
                                    continue;

                                //Set temp to true, as a task was put in
                                temp = true;

                                //Calculate the duration at the given random value and add that to duration
                                durations.set(j, durations.get(j) + invG.get(task).getDuration(random.nextDouble() * 100));

                                //Add the task to the hashmap with the time it was done so we can check if
                                //Another task is legal
                                taskDoneAt.put(task, durations.get(j));

                                //Add the task to the list of tasks done so we can check if another task is legal
                                tasksDone.add(task);

                                //If the duration is no longer the shortest in the list of durations
                                //Go to another duration
                                if (!(durations.indexOf(Collections.min(durations)) == j))
                                    break;
                            }
                        }

                        //If there were no task able to be put in, increment the durations list to the
                        //Second lowest so we can find a legal time
                        if (!temp) {
                            MonteCarlo.allLowestToSecondLowest(durations);
                        }
                    }
                }

                //Add the duration of the project to the double that will be used to calculate the average duration
                duration += durations.get(durations.indexOf(Collections.max(durations)));

            } else {
                //For each task in the taskList
                for (Task task : taskList) {

                    //Calculate the duration at the given random value and add that to duration
                    double temp = invG.get(task).getDuration(random.nextDouble() * 100);
                    duration += temp;

                }
            }

            //Try to insert the chance of completion at the certain time
            //If it fails, initialize the spot in the list to 1, as 1 duration has been this long
            //(i.e. this run-through)
            try {
                chances.set((int) Math.round((duration / (i + 1))),
                        chances.get((int) Math.round((duration / (i + 1)))) + 1);
            } catch (IndexOutOfBoundsException e) {
                while (chances.size() < (int) Math.round((duration / (i + 1))))
                    chances.add(0d);
                chances.add((int) Math.round((duration / (i + 1))), 1d);
            }

        }

        //Send an estimate back with the chances at each time and the duration of the project which will be used
        //To calculate the estimated time of the project
        return new Estimate(chances, duration);
    }
}
