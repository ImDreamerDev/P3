package dk.aau.ds304e18.estimatetime;

import dk.aau.ds304e18.math.InverseGaussian;
import dk.aau.ds304e18.models.Task;

import java.util.*;
import java.util.concurrent.Callable;

public class EstimateTimeCallable implements Callable<Estimate> {
    private final List<Task> taskList;
    private final double amountEmployees;
    private final int numOfThreads;
    private final int numOfMonte;
    private final Random random = new Random();
    private HashMap<Task, InverseGaussian> invG = new HashMap<>();
    //private final InverseGaussian invG = new InverseGaussian();

    public EstimateTimeCallable(List<Task> taskList, double amountEmployees, int numOfThreads, int numOfMonte) {
        this.taskList = taskList;
        this.amountEmployees = amountEmployees;
        this.numOfThreads = numOfThreads;
        this.numOfMonte = numOfMonte;
    }

    public Estimate call() {
        List<Double> chances = new ArrayList<>();
        HashMap<Task, Double> taskStartAt = new HashMap<>();
        HashMap<Task, Double> taskEndAt = new HashMap<>();
        for(Task task : taskList)
            invG.put(task, task.getInvG());

        double duration = 0.0;
        int repeats = numOfMonte / numOfThreads;
        //Repeat repeats time
        for (int i = 0; i < repeats; i++) {
            if (amountEmployees > 1) {
                List<Task> tasksDone = new ArrayList<>();
                List<Double> durations = new ArrayList<>();
                HashMap<Task, Double> taskDoneAt = new HashMap<>();

                for (int j = 0; j < amountEmployees; j++) {
                    if (j >= taskList.size()) break;
                    durations.add(0d);
                }

                while (taskList.size() != tasksDone.size()) {
                    for (int j = 0; j < amountEmployees && j < taskList.size(); j++) {
                        if (taskList.size() == tasksDone.size()) break;
                        if (0 + durations.get(j) != 0 + Collections.min(durations) && durations.get(j) != 0) continue;
                        boolean temp = false;
                        for (Task task : taskList) {
                            if (tasksDone.contains(task)) continue;

                            if (tasksDone.containsAll(task.getDependencies())) {
                                boolean skip = false;

                                for (Task dependency : task.getDependencies()) {
                                    if (taskDoneAt.get(dependency) > durations.get(j))
                                        skip = true;
                                }

                                if (skip)
                                    continue;

                                temp = true;

                                if(taskStartAt.containsKey(task)) {
                                    taskStartAt.put(task, taskStartAt.get(task) + durations.get(j));
                                } else {
                                    taskStartAt.put(task, durations.get(j));
                                }

                                //Create a random double between 0 and 100
                                double rand = random.nextDouble() * 100;

                                //Create an inverse gaussian distribution for the task
                                //Consider putting the InverseGaussian as an element of each task so we don't have to setParams on each task 10000*200 times
                                //invG.setParams(task.getEstimatedTime(), task.getLambda());

                                //Calculate the duration at the given random value and add that to duration
                                durations.set(j, durations.get(j) + invG.get(task).getDuration(rand));

                                if(taskEndAt.containsKey(task)) {
                                    taskEndAt.put(task, taskEndAt.get(task) + durations.get(j));
                                } else {
                                    taskEndAt.put(task, durations.get(j));
                                }

                                taskDoneAt.put(task, durations.get(j));
                                tasksDone.add(task);

                                if (!(durations.indexOf(Collections.min(durations)) == j))
                                    break;
                            }
                        }

                        if (!temp) {
                            int index = 0;

                            Collections.sort(durations);

                            for (Double tempDur : durations) {
                                if (tempDur > durations.get(0)) {
                                    index = durations.indexOf(tempDur);
                                    break;
                                }
                            }

                            double minUpper = durations.get(index);

                            for (int k = 0; k < index; k++)
                                durations.set(k, minUpper);
                        }
                    }
                }

                duration += durations.get(durations.indexOf(Collections.max(durations)));

            } else {

                double currentTime = 0d;

                //For each task in the taskList
                for (Task task : taskList) {

                    //Create a random double between 0 and 100
                    double rand = random.nextDouble() * 100;

                    //Adds startpoint of task
                    if(taskStartAt.containsKey(task))
                        taskStartAt.put(task, taskStartAt.get(task) + currentTime);
                    else
                        taskStartAt.put(task, currentTime);

                    //Create an inverse gaussian distribution for the task
                    //invG.setParams(task.getEstimatedTime(), task.getLambda());

                    //Calculate the duration at the given random value and add that to duration
                    double temp = invG.get(task).getDuration(rand);
                    duration += temp;
                    currentTime += temp;

                    if(taskEndAt.containsKey(task)) {
                        taskEndAt.put(task, taskEndAt.get(task) + currentTime);
                    } else {
                        taskEndAt.put(task, currentTime);
                    }
                }
            }

            try {
                chances.set((int) Math.round((duration / (i + 1))),
                        chances.get((int) Math.round((duration / (i + 1)))) + 1);
            } catch (IndexOutOfBoundsException e) {
                while (chances.size() < (int) Math.round((duration / (i + 1))))
                    chances.add(0d);
                chances.add((int) Math.round((duration / (i + 1))), 1d);
            }

        }
        //Also send the start time of each task back
        final Estimate estimate = new Estimate(chances, taskStartAt, taskEndAt, duration);
        return estimate;
    }
}
